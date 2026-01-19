<?php
require __DIR__ . '/config.php';

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if (!isset($_GET['user_id'])) {
        respond(false, 'Missing user_id');
    }
    $userId = (int)$_GET['user_id'];
    fetch_user($mysqli, $userId);

    $stmt = $mysqli->prepare('SELECT id, user_id, activity, duration_minutes, distance_km, calories, notes, location, recorded_at FROM workouts WHERE user_id = ? ORDER BY recorded_at DESC');
    $stmt->bind_param('i', $userId);
    $stmt->execute();
    $result = $stmt->get_result();
    $data = [];
    while ($row = $result->fetch_assoc()) {
        $data[] = [
            'id' => (int)$row['id'],
            'user_id' => (int)$row['user_id'],
            'activity' => $row['activity'],
            'duration_minutes' => (int)$row['duration_minutes'],
            'distance_km' => $row['distance_km'] !== null ? (float)$row['distance_km'] : null,
            'calories' => (int)$row['calories'],
            'notes' => $row['notes'],
            'location' => $row['location'],
            'recorded_at' => $row['recorded_at'],
        ];
    }
    respond(true, 'Workouts fetched', $data);
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $required = require_fields(['user_id', 'activity', 'duration_minutes', 'calories'], $_POST);
    $userId = (int)$required['user_id'];
    fetch_user($mysqli, $userId);

    $activity = allowed_activity($required['activity']);
    $duration = (int)$required['duration_minutes'];
    $calories = (int)$required['calories'];
    $distance = isset($_POST['distance_km']) && $_POST['distance_km'] !== '' ? (float)$_POST['distance_km'] : null;
    $notes = $_POST['notes'] ?? null;
    $location = $_POST['location'] ?? null;

    $stmt = $mysqli->prepare('INSERT INTO workouts(user_id, activity, duration_minutes, distance_km, calories, notes, location) VALUES (?, ?, ?, ?, ?, ?, ?)');
    $stmt->bind_param('isidiss', $userId, $activity, $duration, $distance, $calories, $notes, $location);
    $stmt->execute();

    respond(true, 'Workout saved', [
        'id' => $stmt->insert_id,
        'user_id' => $userId,
        'activity' => $activity,
        'duration_minutes' => $duration,
        'distance_km' => $distance,
        'calories' => $calories,
        'notes' => $notes,
        'location' => $location,
        'recorded_at' => date('c'),
    ]);
}

respond(false, 'Unsupported HTTP method', null, 405);
?>