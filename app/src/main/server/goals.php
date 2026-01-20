<?php
require_once 'config.php';
require_once 'helpers.php';

auth_required();

$method = $_SERVER['REQUEST_METHOD'];
$action = $_GET['action'] ?? '';

/* CREATE a new goal */
if ($method === 'POST' && $action === 'create') {
    $data = json_input();
    require_fields($data, ['title','period']);

    $stmt = $pdo->prepare("
        INSERT INTO goals
        (user_id, title, period, duration_minutes, start_date, end_date, created_at, updated_at)
        VALUES (?,?,?,?,?,?,NOW(),NOW())
    ");

    $stmt->execute([
        $_SESSION['user']['id'],
        $data['title'],
        $data['period'], // day | month | duration
        $data['duration_minutes'] ?? null,
        $data['start_date'] ?? null,
        $data['end_date'] ?? null
    ]);

    echo json_encode(['message' => 'Goal created']);
    exit;
}

/*LIST all goals for current user */
if ($method === 'GET' && $action === 'list') {
    $stmt = $pdo->prepare("
        SELECT id, title, period, duration_minutes, start_date, end_date, status, created_at, updated_at
        FROM goals
        WHERE user_id = ?
        ORDER BY created_at DESC
    ");
    $stmt->execute([$_SESSION['user']['id']]);
    echo json_encode($stmt->fetchAll(PDO::FETCH_ASSOC));
    exit;
}

/*UPDATE a goal */
if ($method === 'PUT' && $action === 'update') {
    $data = json_input();
    require_fields($data, ['id','title','period']);

    $stmt = $pdo->prepare("
        UPDATE goals
        SET title = ?, period = ?, duration_minutes = ?, start_date = ?, end_date = ?, updated_at = NOW()
        WHERE user_id = ? AND id = ?
    ");

    $stmt->execute([
        $data['title'],
        $data['period'],
        $data['duration_minutes'] ?? null,
        $data['start_date'] ?? null,
        $data['end_date'] ?? null,
        $_SESSION['user']['id'],
        $data['id']
    ]);

    echo json_encode(['message' => 'Goal updated']);
    exit;
}


/* DELETE a goal */
if ($method === 'DELETE' && $action === 'delete') {
    $raw = file_get_contents("php://input");
    $data = json_decode($raw, true);
    require_fields($data, ['id']);

    $stmt = $pdo->prepare("
        DELETE FROM goals
        WHERE user_id = ? AND id = ?
    ");
    $stmt->execute([
        $_SESSION['user']['id'],
        $data['id']
    ]);

    if ($stmt->rowCount() > 0) {
        echo json_encode(['message' => 'Goal deleted']);
    } else {
        http_response_code(404);
        echo json_encode(['error' => 'Goal not found']);
    }
    exit;
}


/*RESET a goal */
if ($method === 'POST' && $action === 'reset') {
    $data = json_input();
    require_fields($data, ['id']);

    $stmt = $pdo->prepare("
        UPDATE goals
        SET status = 'reset', updated_at = NOW()
        WHERE user_id = ? AND id = ?
    ");
    $stmt->execute([
        $_SESSION['user']['id'],
        $data['id']
    ]);

    echo json_encode(['message' => 'Goal reset']);
    exit;
}


http_response_code(404);
echo json_encode(['error' => 'Not found']);
