<?php
function json_input() {
    $raw = file_get_contents('php://input');
    return $raw ? json_decode($raw, true) : [];
}

function require_fields($data, $fields) {
    foreach ($fields as $f) {
        if (!isset($data[$f]) || $data[$f] === '') {
            http_response_code(422);
            echo json_encode(['error' => "Missing field: $f"]);
            exit;
        }
    }
}

function auth_required() {
    if (!isset($_SESSION['user'])) {
        http_response_code(401);
        echo json_encode(['error' => 'Unauthorized']);
        exit;
    }
}

function is_admin() {
    return isset($_SESSION['user']) && $_SESSION['user']['role'] === 'admin';
}

function age_from_dob($dob) {
    $birth = new DateTime($dob);
    $today = new DateTime();
    return $today->diff($birth)->y;
}






