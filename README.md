# Tubes1_greedisgood
Code ini dibuat untuk memenuhi **Tugas Besar 1 IF2211 Strategi Algoritma**, Pengimplementasian Strategi Algoritma *Greedy* pada bot di Game [**GALAXIO**](https://github.com/EntelectChallenge/2021-Galaxio)
## Pengimplementasian Algoritma Greedy
### Alternatif Solusi Greedy
Pada bot yang kami buat kami menggunakan serangkaian algoritma *greedy* untuk mampu melakukan beberapa mekanisme yang dibutuhkan sesuai dengan kondisi dalam pertandingan
1. *Greedy by Point*
Mengutamakan mencari *food* terdekat dari kapal dengan mempertimbangkan kondisi sekitar *food* tersebut. Jika *food* tersebut didekat musuh dengan size yang lebih besar atau berada dalam suatu *obstacle* maka kapal akan mencari *food* lain yang lebih aman.
2. Mekanisme Pertahanan
Jika *makanan* yang dituju kapal berada di obstacle maka kapal akan mencari *food*, selain itu juga jika suatu *projectile* mengarah ke kapal maka kapal akan mengaktifkan *shield*
3. Mekanisme Penyerangan
Mekanisme penyerangan dapat diaktifkan oleh kapal jika memenuhi beberapa kondisi, contohnya jika *size* kapal lebih dari 30 dan jarak antara kapal dengan musuh kurang dari 125 maka kapal akan menembakkan *torpedo*, namun jika jarak kapal dengan musuh melebihi 125 maka kapal akan menembakkan *teleporter* yang mampu mengurangi kapal dengan musuh.
## Requirement program dan instalasi
Ada serangkaian *requirement* dan instalasi yang perlu dilakukan agar dapat menjalankan *bot* ini dalam permainan **GALAXIO**, yaitu:
- Instalasi
  - [Galaxio Starter-Pack](https://github.com/EntelectChallenge/2021-Galaxio/releases/tag/2021.3.2)
  - [.NET Core 3.1.426](https://dotnet.microsoft.com/en-us/download/dotnet/3.1)
  - [.NET Core 5,0](https://dotnet.microsoft.com/en-us/download/dotnet/5.0)
  - [NodeJS](https://nodejs.org/en/download/)
  - [Java 11 (minimum)](https://www.oracle.com/java/technologies/downloads/#java)
  - [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- Requirement
  1. Membuat file run.bat pada folder ./starter-pack dan mengcopy kode ini (Baca sidenote pada halaman paling bawah readme)[^1]
  ```
  @echo off
  :: Game Runner
  cd ./runner-publish/
  start "" dotnet GameRunner.dll

  :: Game Engine
  cd ../engine-publish/
  timeout /t 1
  start "" dotnet Engine.dll

  :: Game Logger
  cd ../logger-publish/
  timeout /t 1
  start "" dotnet Logger.dll

  :: Bots
  cd ../reference-bot-publish/
  timeout /t 3
  start "" dotnet ReferenceBot.dll
  timeout /t 3
  start "" dotnet ReferenceBot.dll
  timeout /t 3
  start "" dotnet ReferenceBot.dll
  timeout /t 3
  start "" dotnet ReferenceBot.dll
  cd ../

  pause 
  ```
  2. Lakukan ekstrak pada file .zip **GALAXIO** dalam folder "visualiser" sesuai dengan OS anda
  3. Jalankan aplikasi **GALAXIO**
  4. Buka menu "Options"
  5. Salin path folder "logger-publish" pada "Log Files Location", lalu "Save"
  6. Lalu keluar dari Game
## Langkah-langkah menjalankan program  
  1. Jalankan run.bat
  2. Tunggu hingga proses pada semua terminal berhenti dan tutup semua terminal yang terbuka
  3. Buka kembali Game
  4. Buka menu "Load"
  5. Pilih file JSON yang ingin diload pada “Game Log”, lalu “Start”
  6. Setelah masuk ke visualisasinya, kalian dapat melakukan start, pause, rewind, dan reset

Selamat!! Program sudah bisa berjalan dan anda bisa melihat kinerja dari bot kami
## Author/Identitas Pembuat Program
- 13521012 / Haikal Ardzi Shofiyyurrohman
- 13521013 / Eunice Sarah Siregar
- 13521029 / Muhammad Malik Ibrahim Baharsyah

[^1]: dotnet ReferenceBot.dll dapat diganti dengan **java -jar pathFile/greedisgood.jar**
