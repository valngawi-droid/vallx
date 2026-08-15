# ChipApp v3 Native

ChipApp v3 adalah aplikasi Android **native** (Java/Android Views, tanpa WebView) dengan pengalaman antarmuka bergaya iOS. Aplikasi berkomunikasi dengan server Node.js yang berjalan di Termux.

## Instal APK

Build APK release dengan Android Studio atau jalankan `scripts/build-signed.sh`. Hasilnya adalah `release/ChipApp-release.apk`. Izinkan *Install unknown apps* pada ponsel, buka file APK, lalu tekan **Instal**.

## Server Termux

```bash
pkg update
pkg install nodejs git
git clone -b main https://github.com/valngawi-droid/vallx.git
cd vallx/termux
npm start
```

Cari IP ponsel Termux dengan `ip addr`, misalnya `192.168.1.20`. Di ChipApp buka **Pengaturan → Alamat Server Termux**, lalu isi `http://192.168.1.20:3000`.

Jika APK dan Termux berada pada ponsel yang sama, gunakan `http://127.0.0.1:3000`.

## Build lokal

Membutuhkan JDK 17 dan Android SDK 35:

```bash
gradle :app:assembleDebug
```

Hasil debug berada di `app/build/outputs/apk/debug/app-debug.apk`.
