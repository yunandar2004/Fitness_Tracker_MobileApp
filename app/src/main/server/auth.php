<?php
require_once 'config.php';
require_once 'helpers.php';

$method = $_SERVER['REQUEST_METHOD'];
$path = $_GET['action'] ?? '';

if ($method === 'POST' && $path === 'register') {
    $data = json_input();
    require_fields($data, ['name','email','password','date_of_birth','current_weight_kg','gender']);

    // email unique check
    $stmt = $pdo->prepare("SELECT id FROM users WHERE email = ?");
    $stmt->execute([$data['email']]);
    if ($stmt->fetch()) {
        http_response_code(409);
        echo json_encode(['error' => 'Email already exists']);
        exit;
    }

    $hash = password_hash($data['password'], PASSWORD_DEFAULT);
    $stmt = $pdo->prepare("INSERT INTO users (name,email,password_hash,date_of_birth,current_weight_kg,gender,role,profile_image) VALUES (?,?,?,?,?,?, 'user', '')");
    $stmt->execute([
        $data['name'],
        $data['email'],
        $hash,
        $data['date_of_birth'],
        $data['current_weight_kg'],
        $data['gender']
    ]);

    echo json_encode(['message' => 'Registered successfully']);
    exit;
}

if ($method === 'POST' && $path === 'login') {
    $data = json_input();
    require_fields($data, ['email','password']);

    $stmt = $pdo->prepare("SELECT id,name,email,password_hash,role,date_of_birth,current_weight_kg,gender,profile_image FROM users WHERE email = ? AND deleted_at IS NULL");
    $stmt->execute([$data['email']]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$user || !password_verify($data['password'], $user['password_hash'])) {
        http_response_code(401);
        echo json_encode(['error' => 'Invalid credentials']);
        exit;
    }

    unset($user['password_hash']);
    $_SESSION['user'] = $user;

    // record session login
    $stmt = $pdo->prepare("INSERT INTO sessions (user_id) VALUES (?)");
    $stmt->execute([$user['id']]);

    echo json_encode([
        'message' => 'Login successful',
        'role' => $user['role'],
        'dashboard' => $user['role'] === 'admin' ? 'admindashboard' : 'userdashboard'
    ]);
    exit;
}

if ($method === 'POST' && $path === 'logout') {
    auth_required();
    // update latest session logout_time
    $stmt = $pdo->prepare("UPDATE sessions SET logout_time = NOW() WHERE user_id = ? AND logout_time IS NULL ORDER BY id DESC LIMIT 1");
    $stmt->execute([$_SESSION['user']['id']]);

    session_destroy();
    echo json_encode(['message' => 'Logged out']);
    exit;
}

http_response_code(404);
echo json_encode(['error' => 'Not found']);
