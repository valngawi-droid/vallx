# Menjalankan ChipApp di Termux

## Instalasi pertama

```bash
pkg update
pkg install nodejs git

git clone <URL_REPOSITORY> chipapp
cd chipapp
npm install
npm run build
npm start
```

ChipApp aktif di `http://localhost:3000`. Untuk membuka dari perangkat lain dalam Wi-Fi yang sama, cari IP ponsel dengan `ip addr` lalu buka `http://IP_PONSEL:3000`.

## Mengganti port

```bash
PORT=8080 npm start
```

## Menjaga server tetap aktif

```bash
pkg install tmux
tmux new -s chipapp
npm start
```

Tekan `Ctrl+B`, lalu `D` untuk meninggalkan sesi tanpa mematikan server. Kembali dengan `tmux attach -t chipapp`.

## Update aplikasi

```bash
git pull
npm install
npm run build
npm start
```

Server dibuat dengan modul bawaan Node.js sehingga tidak membutuhkan Express dan ringan untuk Termux.
