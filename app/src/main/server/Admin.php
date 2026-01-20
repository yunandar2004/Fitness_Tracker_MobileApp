<?php
require_once 'User.php';

class Admin extends User {

    // Delete a user (soft delete)
    public function deleteUser(int $userId): bool {
        $stmt = $this->pdo->prepare("UPDATE users SET deleted_at = NOW() WHERE id=?");
        return $stmt->execute([$userId]);
    }

    // Change a user's role
    public function changeUserRole(int $userId, string $role): bool {
        $stmt = $this->pdo->prepare("UPDATE users SET role=?, updated_at=NOW() WHERE id=?");
        return $stmt->execute([$role, $userId]);
    }

    // Fetch all users
    public function getAllUsers(): array {
        $stmt = $this->pdo->query("SELECT id, name, email, role, gender, current_weight_kg, created_at, deleted_at FROM users ORDER BY created_at DESC");
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }
}
