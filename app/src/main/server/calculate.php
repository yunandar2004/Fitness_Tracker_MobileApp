<?php
require_once 'config.php';
require_once 'helpers.php';

// auth_required(); // optional

$method = $_SERVER['REQUEST_METHOD'];
if ($method !== 'POST') {
    http_response_code(405);
    echo json_encode(['error' => 'Use POST']);
    exit;
}

$data = json_input();
require_fields($data, ['activity', 'time_minutes', 'level']);

$metValues = [
    'Running' => ['8' => 8.3, '10' => 10.0, '12' => 11.5],
    'Cycling' => ['16' => 6.8, '20' => 8.0, '25' => 11.0],
    'Weightlifting' => ['light' => 3.0, 'moderate' => 6.0, 'vigorous' => 8.0],
    'Yoga' => ['hatha' => 2.5, 'power' => 4.0],
    'Swimming' => ['freestyle' => 8.0, 'breaststroke' => 10.0, 'butterfly' => 13.8],
    'Walking' => ['3' => 2.8, '5' => 3.8, '6.5' => 5.0],
    'HIIT' => ['moderate' => 8.0, 'intense' => 11.0, 'extreme' => 14.0]
];

$activity = $data['activity'];
$time = (int)$data['time_minutes'];
$level = $data['level'];
$weight = isset($data['weight'])
    ? (float)$data['weight']
    : (float)$_SESSION['user']['current_weight_kg'];

if (!isset($metValues[$activity][$level])) {
    http_response_code(422);
    echo json_encode(['error' => 'Invalid activity or level']);
    exit;
}

$met = $metValues[$activity][$level];
$calories = $met * $weight * ($time / 60.0);

echo json_encode([
    'burned_calories' => round($calories, 2)
]);
