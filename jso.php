<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JSO Generator</title>
    <link href="https://fonts.googleapis.com/css?family=Fredoka+One&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css?family=Caveat&display=swap" rel="stylesheet">
    <style>
        body {
            background-color: #2C3A49;
            color: white;
            font-family: 'Fredoka One', cursive;
            margin: 0;
            padding: 20px;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            text-align: center;
        }
        .head {
            text-shadow: 4px 1px red;
            font-size: 2.5em;
            margin-bottom: 20px;
        }
        .textarea {
            width: 90%;
            height: 120px;
            border-radius: 10px;
            border: none;
            padding: 12px;
            font-family: monospace;
            resize: vertical;
        }
        .btn {
            border-radius: 5px;
            background-color: #ffffff;
            color: #2C3A49;
            border: none;
            padding: 10px 25px;
            font-family: 'Fredoka One', cursive;
            font-size: 16px;
            cursor: pointer;
            margin: 10px 0;
            transition: 0.2s;
        }
        .btn:hover {
            background-color: #e0e0e0;
        }
        .foot {
            font-family: 'Caveat', cursive;
            text-shadow: 4px 1px red;
            margin-top: 30px;
        }
        .ress {
            font-size: 22px;
            color: aqua;
            margin-top: 20px;
        }
        .result-input {
            width: 90%;
            padding: 10px;
            border-radius: 5px;
            border: none;
            font-family: monospace;
            margin-bottom: 10px;
            background-color: #1e2631;
            color: #00ffff;
            text-align: center;
        }
    </style>
    <script>
        function copyText() {
            var copyInput = document.getElementById("pilih");
            copyInput.select();
            copyInput.setSelectionRange(0, 99999);
            document.execCommand("copy");
            alert("Script berhasil disalin!");
        }
    </script>
</head>
<body>

<div class="container">
    <h1 class="head">Tools JSO Generator</h1>

    <form method="POST" action="">
        <textarea name="input" class="textarea" placeholder="Masukan Script/HTML Yang Mau Di Pake" required></textarea><br>
        <input type="submit" name="submit" value="Submit" class="btn"><br>
    </form>

    <?php 
    if (isset($_POST['submit'])) {
        $input_code = $_POST['input'];

        if (!empty($input_code)) {
            // 1. Buat ID acak unik untuk nama file
            $unique_id = substr(md5(uniqid(rand(), true)), 0, 8);
            
            // 2. Format isi file menjadi JavaScript document.write
            $js_payload = "document.write(" . json_encode($input_code) . ");";
            
            // 3. Buat folder 'raw' jika belum ada
            $folder = 'raw/';
            if (!is_dir($folder)) {
                mkdir($folder, 0777, true);
            }
            
            // 4. Simpan file JavaScript ke folder 'raw'
            $file_path = $folder . $unique_id . ".js";
            file_put_contents($file_path, $js_payload);
            
            // 5. Susun URL lengkap ke file JavaScript
            $protocol = (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on') ? "https" : "http";
            $host = $_SERVER['HTTP_HOST'];
            $full_url = $protocol . "://" . $host . "/" . $file_path;
            
            // 6. Buat tag pemanggil script
            $result_tag = "<script src='" . $full_url . "'></script>";
            
            echo "<h2 class='ress'>Result:</h2>";
            echo "<input type='text' value='" . htmlspecialchars($result_tag) . "' id='pilih' class='result-input' readonly /><br>";
            echo "<button type='button' onclick='copyText()' class='btn'>Copy</button>";
        }
    }
    ?>

    <h2 class="foot">@2k19 Coded By Nor Ahmad</h2>
</div>

</body>
</html>
