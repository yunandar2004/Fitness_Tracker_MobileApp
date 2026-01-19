<?php
require __DIR__ . '/config.php';

require_method('GET');

if (!isset($_GET['user_id'])) {
    respond(false, 'Missing user_id');
}
$userId = (int)$_GET['user_id'];
fetch_user($mysqli, $userId);

$stmt = $mysqli->prepare('SELECT COUNT(*) AS workouts, COALESCE(SUM(duration_minutes), 0) AS minutes, COALESCE(SUM(distance_km), 0) AS distance, COALESCE(SUM(calories), 0) AS calories FROM workouts WHERE user_id = ?');
$stmt->bind_param('i', $userId);
$stmt->execute();
$result = $stmt->get_result()->fetch_assoc();

respond(true, 'Progress summary', [
    'total_workouts' => (int)$result['workouts'],
    'total_minutes' => (int)$result['minutes'],
    'distance_km' => (float)$result['distance'],
    'calories' => (int)$result['calories'],
]);
?>