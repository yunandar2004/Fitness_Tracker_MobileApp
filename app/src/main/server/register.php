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
require_fields($data, ['name','email','password','date_of_birth','current_weight_kg','gender']);

$success = User::register(
    $pdo,
    $data['name'],
    $data['email'],
    $data['password'],
    $data['date_of_birth'],
    (float)$data['current_weight_kg'],
    $data['gender']
);

if ($success) {
    echo json_encode(['message'=>'Registered successfully']);
} else {
    http_response_code(409);
    echo json_encode(['error'=>'Email already exists']);
}
