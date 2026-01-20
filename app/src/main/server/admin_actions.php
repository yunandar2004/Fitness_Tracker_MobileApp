<?php
require_once 'config.php';
require_once 'User.php';
require_once 'Admin.php';
require_once 'helpers.php';

auth_required();

// Ensure current user is admin
$currentUser = User::loadById($pdo, $_SESSION['user']['id']);
if ($currentUser->getRole() !== 'admin') {
    http_response_code(403);
    echo json_encode(['error' => 'Admin only']);
    exit;
}

$admin = new Admin($pdo, $_SESSION['user']); // Admin object

$method = $_SERVER['REQUEST_METHOD'];
$action = $_GET['action'] ?? '';

if ($method === 'GET' && $action === 'users') {
    echo json_encode($admin->getAllUsers());
    exit;
}

if ($method === 'PUT' && $action === 'role') {
    $data = json_input();
    require_fields($data, ['user_id','role']);
    if ($admin->changeUserRole($data['user_id'], $data['role'])) {
        echo json_encode(['message' => 'Role updated']);
    } else {
        http_response_code(500);
        echo json_encode(['error' => 'Failed']);
    }
    exit;
}

if ($method === 'DELETE' && $action === 'delete_user') {
    $data = json_input();
    require_fields($data, ['user_id']);
    if ($admin->deleteUser($data['user_id'])) {
        echo json_encode(['message' => 'User deleted']);
    } else {
        http_response_code(500);
        echo json_encode(['error' => 'Failed']);
    }
    exit;
}
