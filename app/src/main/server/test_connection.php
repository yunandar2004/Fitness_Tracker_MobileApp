<?php
require 'config.php';

try {
    $pdo->query("SELECT 1");
    echo json_encode(['success' => true, 'message' => 'DB connection OK']);
} catch (Exception $e) {
    echo json_encode(['success' => false, 'message' => 'Unable to reach DB: ' . $e->getMessage()]);
}
