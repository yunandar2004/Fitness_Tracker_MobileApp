<?php
require __DIR__ . '/config.php';

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if (!isset($_GET['user_id'])) {
        respond(false, 'Missing user_id');
    }
    $userId = (int)$_GET['user_id'];
    fetch_user($mysqli, $userId);

    $stmt = $mysqli->prepare('SELECT id, user_id, type, target_value, current_value, deadline, title, notes, created_at FROM goals WHERE user_id = ? ORDER BY created_at DESC');
    $stmt->bind_param('i', $userId);
    $stmt->execute();
    $result = $stmt->get_result();

    $goals = [];
    while ($row = $result->fetch_assoc()) {
        $goals[] = map_goal_row($row);
    }
    respond(true, 'Goals fetched', $goals);
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $required = require_fields(['user_id', 'type', 'target_value', 'deadline', 'title'], $_POST);
    $userId = (int)$required['user_id'];
    fetch_user($mysqli, $userId);

    $type = allowed_goal_type($required['type']);
    $targetValue = (float)$required['target_value'];
    $deadline = $required['deadline'];
    $title = $required['title'];
    $notes = $_POST['notes'] ?? null;
    $goalId = isset($_POST['goal_id']) && $_POST['goal_id'] !== '' ? (int)$_POST['goal_id'] : null;

    if ($goalId) {
        $stmt = $mysqli->prepare('UPDATE goals SET type = ?, target_value = ?, deadline = ?, title = ?, notes = ? WHERE id = ? AND user_id = ?');
        $stmt->bind_param('sdsssii', $type, $targetValue, $deadline, $title, $notes, $goalId, $userId);
        $stmt->execute();
    } else {
        $stmt = $mysqli->prepare('INSERT INTO goals(user_id, type, target_value, current_value, deadline, title, notes) VALUES (?, ?, ?, 0, ?, ?, ?)');
        $stmt->bind_param('isdsss', $userId, $type, $targetValue, $deadline, $title, $notes);
        $stmt->execute();
    }

    $stmt = $mysqli->prepare('SELECT id, user_id, type, target_value, current_value, deadline, title, notes, created_at FROM goals WHERE user_id = ? ORDER BY created_at DESC');
    $stmt->bind_param('i', $userId);
    $stmt->execute();
    $result = $stmt->get_result();
    $goals = [];
    while ($row = $result->fetch_assoc()) {
        $goals[] = map_goal_row($row);
    }

    respond(true, $goalId ? 'Goal updated' : 'Goal created', $goals);
}

respond(false, 'Unsupported HTTP method', null, 405);
?>