<?php
class User {
    private PDO $pdo;
    private int $id;
    private string $name;
    private string $email;
    private string $passwordHash;
    private string $role;

    public function __construct(PDO $pdo, array $data) {
        $this->pdo = $pdo;
        $this->id = $data['id'] ?? 0;
        $this->name = $data['name'] ?? '';
        $this->email = $data['email'] ?? '';
        $this->passwordHash = $data['password_hash'] ?? '';
        $this->role = $data['role'] ?? 'user';
    }

    // ---------------------
    // REGISTER NEW USER
    // ---------------------
    public static function register(PDO $pdo, string $name, string $email, string $password, string $dob, float $weight, string $gender): bool {
        // Check if email exists
        $stmt = $pdo->prepare("SELECT id FROM users WHERE email=?");
        $stmt->execute([$email]);
        if ($stmt->fetch()) {
            return false; // email exists
        }

        $hash = password_hash($password, PASSWORD_DEFAULT);
        $stmt = $pdo->prepare("INSERT INTO users (name,email,password_hash,date_of_birth,current_weight_kg,gender,role) VALUES (?,?,?,?,?,?, 'user')");
        return $stmt->execute([$name,$email,$hash,$dob,$weight,$gender]);
    }

    // ---------------------
    // LOGIN USER
    // ---------------------
    public static function login(PDO $pdo, string $email, string $password): ?User {
        $stmt = $pdo->prepare("SELECT * FROM users WHERE email=? AND deleted_at IS NULL");
        $stmt->execute([$email]);
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        if (!$row || !password_verify($password, $row['password_hash'])) {
            return null; // invalid login
        }

        return new User($pdo, $row);
    }

    // ---------------------
    // Load by ID
    // ---------------------
    public static function loadById(PDO $pdo, int $id): ?User {
        $stmt = $pdo->prepare("SELECT * FROM users WHERE id=?");
        $stmt->execute([$id]);
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        return $row ? new User($pdo, $row) : null;
    }

    // ---------------------
    // Getters
    // ---------------------
    public function getId(): int { return $this->id; }
    public function getName(): string { return $this->name; }
    public function getEmail(): string { return $this->email; }
    public function getRole(): string { return $this->role; }
}
