<?php

declare(strict_types=1);

header('Content-Type: application/json; charset=utf-8');

const DB_HOST = '127.0.0.1';
const DB_USER = 'root';
const DB_PASS = '';
const DB_NAME = 'fitness_app';

mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

try {
    $mysqli = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);
    $mysqli->set_charset('utf8mb4');
} catch (mysqli_sql_exception $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Database connection failed',
        'data' => $e->getMessage(),
    ]);
    exit;
}

function respond(bool $success, string $message, $data = null, int $status = 200): void
{
    http_response_code($status);
    echo json_encode([
        'success' => $success,
        'message' => $message,
        'data' => $data,
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

function require_method(string $method): void
{
    if (strcasecmp($_SERVER['REQUEST_METHOD'], $method) !== 0) {
        respond(false, 'Method not allowed', null, 405);
    }
}

function require_fields(array $fields, array $source): array
{
    $values = [];
    foreach ($fields as $field) {
        if (!isset($source[$field]) || trim((string)$source[$field]) === '') {
            respond(false, "Missing field: $field");
        }
        $values[$field] = trim((string)$source[$field]);
    }
    return $values;
}

function create_session(mysqli $db, int $userId): string
{
    $token = bin2hex(random_bytes(32));
    $stmt = $db->prepare('INSERT INTO sessions(user_id, token) VALUES (?, ?)');
    $stmt->bind_param('is', $userId, $token);
    $stmt->execute();
    return $token;
}

function fetch_user(mysqli $db, int $userId): array
{
    $stmt = $db->prepare('SELECT id, name, email, age, weight_kg FROM users WHERE id = ?');
    $stmt->bind_param('i', $userId);
    $stmt->execute();
    $result = $stmt->get_result();
    $user = $result->fetch_assoc();
    if (!$user) {
        respond(false, 'User not found', null, 404);
    }
    return $user;
}

function map_goal_row(array $row): array
{
    return [
        'id' => (int)$row['id'],
        'user_id' => (int)$row['user_id'],
        'type' => $row['type'],
        'target_value' => (float)$row['target_value'],
        'current_value' => (float)$row['current_value'],
        'deadline' => $row['deadline'],
        'title' => $row['title'],
        'notes' => $row['notes'],
        'created_at' => $row['created_at'],
    ];
}

function allowed_activity(string $activity): string
{
    $normalized = strtolower(trim($activity));
    $map = [
        'running' => 'running',
        'run' => 'running',
        'walking' => 'running',
        'cycle' => 'cycling',
        'cycling' => 'cycling',
        'bike' => 'cycling',
        'weightlifting' => 'weightlifting',
        'weights' => 'weightlifting',
        'lifting' => 'weightlifting',
        'yoga' => 'yoga',
        'swimming' => 'swimming',
        'swim' => 'swimming',
        'hiit' => 'running',
    ];
    if (!array_key_exists($normalized, $map)) {
        respond(false, 'Unsupported activity value');
    }
    return $map[$normalized];
}

function allowed_goal_type(string $type): string
{
    $normalized = strtolower(trim($type));
    $map = [
        'distance' => 'distance',
        'duration' => 'duration',
        'workouts' => 'workouts',
        'day' => 'workouts',
        'month' => 'workouts',
    ];
    if (!array_key_exists($normalized, $map)) {
        respond(false, 'Unsupported goal type');
    }
    return $map[$normalized];
}
?>