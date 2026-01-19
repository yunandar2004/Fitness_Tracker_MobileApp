<?php
require __DIR__ . '/config.php';

require_method('POST');
$fields = require_fields(['user_id', 'goal_id'], $_POST);
$userId = (int)$fields['user_id'];
$goalId = (int)$fields['goal_id'];
fetch_user($mysqli, $userId);

$stmt = $mysqli->prepare('DELETE FROM goals WHERE id = ? AND user_id = ?');
$stmt->bind_param('ii', $goalId, $userId);
$stmt->execute();

$stmt = $mysqli->prepare('SELECT id, user_id, type, target_value, current_value, deadline, title, notes, created_at FROM goals WHERE user_id = ? ORDER BY created_at DESC');
$stmt->bind_param('i', $userId);
$stmt->execute();
$result = $stmt->get_result();
$goals = [];
while ($row = $result->fetch_assoc()) {
    $goals[] = map_goal_row($row);
}

respond(true, 'Goal deleted', $goals);
?>