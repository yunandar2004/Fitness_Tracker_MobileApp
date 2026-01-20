<?php
require_once 'config.php';
require_once 'helpers.php';

auth_required();

$method = $_SERVER['REQUEST_METHOD'];
$action = $_GET['action'] ?? '';

$metValues = [ /* SAME MET TABLE */ ];

function calc_calories_from_met($weight, $timeMin, $met) {
    return $met * $weight * ($timeMin / 60.0);
}

// ----------------------
// CREATE WORKOUT (POST)
// ----------------------
if ($method === 'POST' && $action === 'create') {
    $data = json_input();
    require_fields($data, ['activity','time_minutes']);

    $activity = $data['activity'];
    $time = (int)$data['time_minutes'];

    if (isset($data['burned_calories'])) {
        $cal = (float)$data['burned_calories'];
    } else {
        require_fields($data, ['level']);
        $key = $data['level'];

        if (!isset($metValues[$activity][$key])) {
            http_response_code(422);
            echo json_encode(['error' => 'Invalid level']);
            exit;
        }

        $weight = (float)$_SESSION['user']['current_weight_kg'];
        $cal = calc_calories_from_met($weight, $time, $metValues[$activity][$key]);
    }

    $stmt = $pdo->prepare(
        "INSERT INTO workouts (user_id, activity, time_minutes, burned_calories, notes)
         VALUES (?,?,?,?,?)"
    );
    $stmt->execute([
        $_SESSION['user']['id'],
        $activity,
        $time,
        $cal,
        $data['notes'] ?? null
    ]);

    echo json_encode([
        'message' => 'Workout saved',
        'burned_calories' => round($cal, 2)
    ]);
    exit;
}

// ----------------------
// GET HISTORY (GET)
// ----------------------
if ($method === 'GET' && $action === 'history') {
    $stmt = $pdo->prepare("SELECT activity, time_minutes, burned_calories, notes, created_at FROM workouts WHERE user_id=? ORDER BY created_at DESC");
    $stmt->execute([$_SESSION['user']['id']]);
    $workouts = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($workouts);
    exit;
}

// ----------------------
// INVALID REQUEST
// ----------------------
http_response_code(400);
echo json_encode(['error' => 'Invalid action']);
exit;
