<?php
require_once 'config.php';
require_once 'User.php';
require_once 'helpers.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['error'=>'Use POST']);
    exit;
}

$data = json_input();
require_fields($data, ['email','password']);

$user = User::login($pdo, $data['email'], $data['password']);

if (!$user) {
    http_response_code(401);
    echo json_encode(['error'=>'Invalid credentials']);
    exit;
}

// save session
$_SESSION['user'] = [
    'id' => $user->getId(),
    'name' => $user->getName(),
    'email' => $user->getEmail(),
    'role' => $user->getRole()
];

echo json_encode(['message'=>'Login successful','role'=>$user->getRole()]);
