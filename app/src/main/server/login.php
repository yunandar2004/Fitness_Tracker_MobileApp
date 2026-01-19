<?php
require __DIR__ . '/config.php';

require_method('POST');
$fields = require_fields(['email', 'password'], $_POST);
$email = strtolower($fields['email']);
$password = $fields['password'];

$stmt = $mysqli->prepare('SELECT id, name, email, password_hash, age, weight_kg FROM users WHERE email = ?');
$stmt->bind_param('s', $email);
$stmt->execute();
$result = $stmt->get_result();
$user = $result->fetch_assoc();

if (!$user || !password_verify($password, $user['password_hash'])) {
    respond(false, 'Invalid credentials', null, 401);
}

$token = create_session($mysqli, (int)$user['id']);

respond(true, 'Logged in', [
    'token' => $token,
    'user' => [
        'id' => (int)$user['id'],
        'name' => $user['name'],
        'email' => $user['email'],
        'age' => $user['age'] !== null ? (int)$user['age'] : null,
        'weight_kg' => $user['weight_kg'] !== null ? (float)$user['weight_kg'] : null,
    ],
]);
?>