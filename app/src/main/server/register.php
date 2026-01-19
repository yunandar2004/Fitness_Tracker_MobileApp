// <?php
// require __DIR__ . '/config.php';
//
// require_method('POST');
// $fields = require_fields(['name', 'email', 'password'], $_POST);
//
// $name = $fields['name'];
// $email = strtolower($fields['email']);
// $password = $fields['password'];
// $age = isset($_POST['age']) && $_POST['age'] !== '' ? (int)$_POST['age'] : null;
// $weight = isset($_POST['current_weight_kg']) && $_POST['current_weight_kg'] !== '' ? (float)$_POST['current_weight_kg'] : null;
//
// $stmt = $mysqli->prepare('SELECT id FROM users WHERE email = ?');
// $stmt->bind_param('s', $email);
// $stmt->execute();
// $stmt->store_result();
// if ($stmt->num_rows > 0) {
//     respond(false, 'Email already registered');
// }
//
// $hash = password_hash($password, PASSWORD_DEFAULT);
// $stmt = $mysqli->prepare('INSERT INTO users(name, email, password_hash, age, weight_kg) VALUES (?, ?, ?, ?, ?)');
// $stmt->bind_param('sssid', $name, $email, $hash, $age, $weight);
// $stmt->execute();
// $userId = $stmt->insert_id;
//
// $token = create_session($mysqli, (int)$userId);
//
// respond(true, 'Registration successful', [
//     'token' => $token,
//     'user' => [
//         'id' => (int)$userId,
//         'name' => $name,
//         'email' => $email,
//         'age' => $age,
//         'weight_kg' => $weight,
//     ],
// ]);
// ?>

<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

require __DIR__ . '/config.php';

// Respond function that always outputs clean JSON
function respond($success, $message, $data = null) {
    if (ob_get_length()) {
        ob_end_clean();
    }
    header('Content-Type: application/json; charset=utf-8');
    $response = ['success' => $success, 'message' => $message];
    if ($data !== null) {
        $response['data'] = $data;
    }
    echo json_encode($response);
    exit;
}

// Ensure POST request
require_method('POST');

// Required fields
$fields = require_fields(['name', 'email', 'password', 'date_of_birth'], $_POST);

$name = trim($fields['name']);
$email = strtolower(trim($fields['email']));
$password = $fields['password'];
$dob = trim($_POST['date_of_birth']); // YYYY-MM-DD
$weight = isset($_POST['current_weight_kg']) && $_POST['current_weight_kg'] !== '' ? (float)$_POST['current_weight_kg'] : 0.0;
$gender = isset($_POST['gender']) ? strtolower(trim($_POST['gender'])) : '';

// Check if email already exists
$stmt = $mysqli->prepare('SELECT id FROM users WHERE email = ?');
$stmt->bind_param('s', $email);
$stmt->execute();
$stmt->store_result();

if ($stmt->num_rows > 0) {
    respond(false, 'Email already registered');
}

// Hash password
$hash = password_hash($password, PASSWORD_DEFAULT);

// Insert user into DB
$stmt = $mysqli->prepare(
    'INSERT INTO users(name, email, password_hash, date_of_birth, current_weight_kg, gender)
     VALUES (?, ?, ?, ?, ?, ?)'
);
$stmt->bind_param('ssssds', $name, $email, $hash, $dob, $weight, $gender);
$stmt->execute();

$userId = (int)$stmt->insert_id;

// Create session token
$token = create_session($mysqli, $userId);

// Return success response
respond(true, 'Registration successful', [
    'token' => $token,
    'user' => [
        'id' => $userId,
        'name' => $name,
        'email' => $email,
        'date_of_birth' => $dob,
        'current_weight_kg' => $weight,
        'gender' => $gender,
    ],
]);
