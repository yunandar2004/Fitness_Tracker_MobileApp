<?php
require __DIR__ . '/config.php';

if ($mysqli->ping()) {
    respond(true, 'DB connection OK');
}
respond(false, 'DB connection failed');
?>