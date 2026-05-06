/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fantasy.shop;

/**
 *
 * @author Asus
 */
import com.mycompany.fantasy.shop.gui.GUIBridge;
import java.util.Scanner;

/**
 * StoryManager.java — versi GUI.
 * Semua pause() dan System.out routing lewat GUIBridge.
 * Cerita ditampilkan sebagai panel narasi dengan tombol "Lanjutkan".
 */
public class StoryManager {
    private static final Scanner scanner = new Scanner(System.in);

    private GUIBridge bridge;

    public void setBridge(GUIBridge bridge) {
        this.bridge = bridge;
    }

    // ── Utility output ─────────────────────────────────────────────
    private void log(String text) {
        if (bridge != null) bridge.appendLog(text);
        else System.out.println(text);
    }

    /**
     * Tampilkan satu blok narasi.
     * GUI: muncul di DecisionPanel dengan tombol Lanjutkan.
     * Console: print + enter.
     */
    private void story(String title, String text, Runnable after) {
        if (bridge != null) {
            bridge.showContinue("[ " + title + " ]\n\n" + text, after);
        } else {
            System.out.println("\n" + "=".repeat(60));
            System.out.println(title);
            System.out.println("=".repeat(60));
            System.out.println(text);
            System.out.print("\n  [ Tekan Enter untuk lanjut... ]");
            scanner.nextLine();
            if (after != null) after.run();
        }
    }

    // ── Dipanggil tiap hari ────────────────────────────────────────
    public void checkStory(int day, Player player) {
        switch (day) {
            case 1  -> storyDay1(null);
            case 3  -> storyDay3(null);
            case 5  -> storyDay5(null);
            case 8  -> storyDay8(null);
            case 10 -> storyDay10(player, null);
            case 13 -> storyDay13(null);
            case 16 -> storyDay16(null);
            case 20 -> storyDay20(player, null);
            case 23 -> storyDay23(null);
            case 27 -> storyDay27(null);
            case 30 -> storyDay30(player, null);
            case 33 -> storyDay33(null);
            case 35 -> storyDay35(null);
            case 38 -> storyDay38(player, null);
            case 40 -> storyDay40(player, null);
        }
    }

    // Versi dengan callback (untuk chaining di GUI)
    public void checkStoryWithCallback(int day, Player player, Runnable afterStory) {
        switch (day) {
            case 1  -> storyDay1(afterStory);
            case 3  -> storyDay3(afterStory);
            case 5  -> storyDay5(afterStory);
            case 8  -> storyDay8(afterStory);
            case 10 -> storyDay10(player, afterStory);
            case 13 -> storyDay13(afterStory);
            case 16 -> storyDay16(afterStory);
            case 20 -> storyDay20(player, afterStory);
            case 23 -> storyDay23(afterStory);
            case 27 -> storyDay27(afterStory);
            case 30 -> storyDay30(player, afterStory);
            case 33 -> storyDay33(afterStory);
            case 35 -> storyDay35(afterStory);
            case 38 -> storyDay38(player, afterStory);
            case 40 -> storyDay40(player, afterStory);
            default -> { if (afterStory != null) afterStory.run(); }
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  ACT 1: AWAL (Hari 1-9)
    // ══════════════════════════════════════════════════════════════

    private void storyDay1(Runnable after) {
        story("PROLOG: WARISAN YANG BERAT",
            "Toko ini bukan sekadar bangunan tua.\n" +
            "Di dinding-dindingnya tersimpan puluhan tahun cerita.\n\n" +
            "Kakek Aldric, pemilik lama, menatapmu dengan mata lelah.\n" +
            "\"Nak... ada yang perlu kamu tahu tentang toko ini.\"\n" +
            "\"Tapi belum sekarang. Kamu belum siap.\"\n\n" +
            "Ia berbalik dan berjalan perlahan meninggalkan desa.\n" +
            "Meninggalkanmu dengan kunci berkarat dan pertanyaan yang belum terjawab.",
            after);
    }

    private void storyDay3(Runnable after) {
        story("HARI KE-3: TETANGGA YANG CURIGA",
            "Kepala Desa Maren menghampirimu di depan toko.\n" +
            "Wajahnya tegang, suaranya pelan.\n\n" +
            "\"Jadi kamu yang meneruskan toko Aldric...\"\n" +
            "\"Hati-hati. Toko itu punya sejarah yang tidak semua orang mau mengingatnya.\"\n\n" +
            "Sebelum sempat bertanya lebih, ia sudah pergi.\n" +
            "Meninggalkan rasa penasaran yang mengganjal.",
            after);
    }

    private void storyDay5(Runnable after) {
        story("HARI KE-5: SURAT TANPA NAMA",
            "Pagi ini ada surat di bawah pintu toko.\n" +
            "Tidak ada nama pengirim. Hanya tiga kalimat:\n\n" +
            "  \"Toko itu dibangun di atas hutang darah.\"\n" +
            "  \"Aldric tahu. Dia selalu tahu.\"\n" +
            "  \"Tinggalkan sebelum terlambat.\"\n\n" +
            "Tanganmu sedikit gemetar membaca baris terakhir.\n" +
            "Tapi kamu melipatnya dan menyimpannya di saku.\n" +
            "Toko ini belum akan kamu tinggalkan.",
            after);
    }

    private void storyDay8(Runnable after) {
        story("HARI KE-8: ANAK KECIL DAN CERITA LAMA",
            "Seorang anak kecil bernama Riko duduk di depan toko.\n" +
            "Ia menatap papan nama toko dengan mata penuh tanya.\n\n" +
            "\"Kak, dulu toko ini pernah terbakar ya?\"\n" +
            "\"Mama bilang dulu ada orang mati di sini.\"\n\n" +
            "Kamu diam. Riko berlari pulang sebelum sempat\n" +
            "kamu bertanya lebih jauh.\n" +
            "Malam itu kamu sulit tidur.",
            after);
    }

    // ══════════════════════════════════════════════════════════════
    //  ACT 2: KONFLIK (Hari 10-29)
    // ══════════════════════════════════════════════════════════════

    private void storyDay10(Player player, Runnable after) {
        story("HARI KE-10: KAKEK ALDRIC KEMBALI",
            "Kakek Aldric muncul di depan pintu tokomu.\n" +
            "Wajahnya lebih tua dari yang kamu ingat.\n\n" +
            "\"Kamu masih di sini. Bagus. Itu berarti kamu kuat.\"\n\n" +
            "Ia bercerita tentang dua puluh tahun lalu.\n" +
            "\"Aku pernah menjual ramuan terlarang...\"\n" +
            "\"Ramuan yang membuat orang kuat, tapi menghancurkan pikiran mereka perlahan-lahan.\"\n\n" +
            "\"Salah satu pembelinya adalah seorang ksatria muda. Namanya Kael.\"\n" +
            "\"Di kotak ini ada catatan tentang obat penawarnya.\"\n" +
            "\"Aku tidak punya cukup waktu untuk menyelesaikannya. Tapi mungkin kamu bisa.\"\n\n" +
            "( +20 EXP dari pemahaman baru )",
            () -> {
                if (player != null) player.gainExp(20);
                if (after != null) after.run();
            });
    }

    private void storyDay13(Runnable after) {
        story("HARI KE-13: KEPALA DESA MEMBUKA DIRI",
            "Kepala Desa Maren datang ke toko, kali ini sendirian.\n" +
            "Ia menutup pintu dan berbicara pelan.\n\n" +
            "\"Aku dengar Aldric sudah cerita padamu.\"\n" +
            "\"Kael... ksatria itu... dia adikku.\"\n\n" +
            "Suaranya pecah di kata terakhir.\n\n" +
            "\"Dulu dia pelindung desa ini. Baik, kuat, disayangi semua.\n" +
            "Tapi setelah minum ramuan itu, dia berubah.\"\n" +
            "\"Sekarang dia berkeliaran di hutan, menyerang siapa saja.\"\n\n" +
            "\"Kalau kamu bisa menemukan obat penawarnya...\n" +
            "Mungkin masih ada harapan untuk adikku.\"",
            after);
    }

    private void storyDay16(Runnable after) {
        story("HARI KE-16: BISIKAN DARI HUTAN",
            "Seorang pedagang lewat mampir ke toko.\n" +
            "Wajahnya pucat, matanya gelisah.\n\n" +
            "\"Di hutan timur... aku melihat sesuatu yang besar.\"\n" +
            "\"Bukan manusia. Bukan binatang biasa.\"\n" +
            "\"Matanya merah. Tubuhnya dibalut rantai berkarat.\"\n\n" +
            "Kamu teringat catatan Aldric tentang Naga Rudoria.\n" +
            "Makhluk purba yang dipenjara oleh sihir gelap.\n" +
            "Sihir yang sama yang ada dalam ramuan terlarang itu.\n\n" +
            "Semuanya mulai terhubung.",
            after);
    }

    private void storyDay20(Player player, Runnable after) {
        story("HARI KE-20: PERTEMUAN DENGAN KAEL",
            "Malam ini Kael muncul di depan toko.\n" +
            "Bukan untuk menyerang. Dia hanya berdiri di sana.\n" +
            "Matanya kosong, tapi di sudutnya ada sesuatu seperti sisa kesadaran.\n\n" +
            "\"Toko... ini...\" suaranya serak seperti kayu terbakar.\n" +
            "\"Aldric... di mana... Aldric...\"\n\n" +
            "Kepala Desa Maren berlari datang dengan obor.\n" +
            "\"Kael! Kael, ini aku, kakakmu!\"\n\n" +
            "Kael memandang Maren sebentar. Sesuatu berkedip di matanya.\n" +
            "Lalu dia berlari kembali ke kegelapan.\n\n" +
            "Maren berlutut di tanah. \"Dia masih ada di sana... masih ada...\"\n\n" +
            "( Reputasi +5 — desa melihat keberanianmu malam ini )",
            () -> {
                if (player != null) player.gainReputation(5);
                if (after != null) after.run();
            });
    }

    private void storyDay23(Runnable after) {
        story("HARI KE-23: CATATAN TERAKHIR ALDRIC",
            "Riko, anak kecil itu, datang ke toko membawa amplop.\n" +
            "\"Pak tua yang rambut putih titip ini untuk kakak.\"\n\n" +
            "Di dalamnya adalah halaman terakhir catatan Aldric:\n\n" +
            "  \"Obat penawarnya ada pada Naga Rudoria.\"\n" +
            "  \"Bukan karena Naga jahat — tapi karena dia yang menanggung kutukan sihir itu.\"\n" +
            "  \"Bebaskan Naga, dan kutukan akan luruh.\"\n" +
            "  \"Kael akan kembali. Desa akan pulih.\"\n\n" +
            "  \"Maafkan aku, nak. Ini semua karena keserakahanku dulu.\"\n\n" +
            "Kamu membaca baris terakhir itu berkali-kali.",
            after);
    }

    private void storyDay27(Runnable after) {
        story("HARI KE-27: DESA DALAM KETAKUTAN",
            "Serangan Kael semakin sering.\n" +
            "Beberapa warga memilih mengungsi ke kota.\n\n" +
            "Kepala Desa Maren mendatangimu.\n" +
            "\"Berapa lama lagi toko ini bisa bertahan?\"\n" +
            "\"Warga butuh harapan. Mereka butuh melihat ada yang tidak menyerah.\"\n\n" +
            "Kamu melihat ke sekeliling toko yang sudah mulai hidup kembali.\n" +
            "Lalu kamu mengangguk.\n\n" +
            "\"Selama aku masih di sini, toko ini tidak akan tutup.\"\n\n" +
            "Maren menatapmu lama. Lalu untuk pertama kali, dia tersenyum.",
            after);
    }

    // ══════════════════════════════════════════════════════════════
    //  ACT 3: RESOLUSI (Hari 30-40)
    // ══════════════════════════════════════════════════════════════

    private void storyDay30(Player player, Runnable after) {
        story("HARI KE-30: MALAM SEBELUM SEGALANYA BERUBAH",
            "Riko duduk di depan toko sambil menggambar.\n" +
            "Kamu duduk di sebelahnya.\n\n" +
            "\"Kak... toko ini bakal baik-baik aja kan?\"\n" +
            "\"Aku suka ke sini. Rasanya... aman.\"\n\n" +
            "Kamu tidak langsung menjawab. Kamu menatap langit malam yang penuh bintang.\n\n" +
            "\"Iya. Bakal baik-baik aja.\"\n\n" +
            "Kamu tidak tahu apakah kamu percaya kata-katamu sendiri.\n" +
            "Tapi kamu tidak akan membiarkan anak ini kehilangan\n" +
            "satu-satunya tempat yang membuatnya merasa aman.\n\n" +
            "( Reputasi +3, EXP +15 — momen ketenangan sebelum badai )",
            () -> {
                if (player != null) { player.gainReputation(3); player.gainExp(15); }
                if (after != null) after.run();
            });
    }

    private void storyDay33(Runnable after) {
        story("HARI KE-33: KAEL MENYERANG DESA",
            "Alarm desa berbunyi di tengah malam.\n" +
            "Kael menerobos masuk ke desa dengan mata merah menyala.\n\n" +
            "Kamu keluar dan berdiri di jalanan.\n" +
            "Kael berhenti di depanmu. Kalian bertatapan.\n\n" +
            "Di balik matanya yang gelap, kamu melihat kilatan kecil —\n" +
            "seperti seseorang yang tenggelam dan mengulurkan tangan minta tolong.\n\n" +
            "\"Aku tahu siapa kamu, Kael,\" katamu pelan.\n" +
            "\"Dan aku akan membebaskanmu.\"\n\n" +
            "Kael menggeram. Tapi dia tidak menyerang. Dia mundur ke kegelapan.\n\n" +
            "\"Bagaimana kamu bisa...\"\n" +
            "\"Tidak ada yang pernah bisa menghentikannya seperti itu.\"",
            after);
    }

    private void storyDay35(Runnable after) {
        story("HARI KE-35: NAGA RUDORIA TERBANGUN",
            "Langit di atas hutan timur memerah. Tanah bergetar pelan.\n\n" +
            "Kepala Desa Maren berlari ke tokomu.\n" +
            "\"Naga itu... dia terbangun. Seluruh desa bisa merasakannya.\"\n\n" +
            "Kamu membuka catatan Aldric untuk terakhir kali.\n" +
            "\"Bebaskan Naga, dan kutukan akan luruh.\"\n\n" +
            "\"Jaga toko ini untukku,\" katamu ke Maren.\n" +
            "\"Aku akan menyelesaikan apa yang Aldric tidak bisa.\"\n\n" +
            "Maren mencengkeram lenganmu.\n" +
            "\"Kembali dengan selamat. Desa ini butuh penjaganya.\"\n\n" +
            "[ Pertarungan dengan Naga Rudoria dimulai... ]",
            after);
    }

    private void storyDay38(Player player, Runnable after) {
        story("HARI KE-38: SETELAH NAGA — KAEL KEMBALI",
            "Tiga hari setelah Naga dibebaskan dari kutukannya,\n" +
            "seseorang mengetuk pintu toko.\n\n" +
            "Seorang pria muda dengan wajah lelah tapi tenang.\n" +
            "Matanya bersih. Tidak ada lagi warna merah di sana.\n\n" +
            "\"Aku... Kael.\"\n" +
            "\"Aku tidak ingat semua yang terjadi.\"\n" +
            "\"Tapi aku tahu seseorang telah menyelamatkanku.\"\n\n" +
            "Kepala Desa Maren muncul dari balik pintu.\n" +
            "Ia berlari dan memeluk adiknya erat-erat. Menangis tanpa malu.\n\n" +
            "Kael memandangmu di atas bahu kakaknya.\n" +
            "\"Terima kasih,\" katanya pelan.\n\n" +
            "( Reputasi +10, Gold +100 — hadiah dari Kepala Desa atas jasamu )",
            () -> {
                if (player != null) { player.gainReputation(10); player.gainGold(100); }
                if (after != null) after.run();
            });
    }

    private void storyDay40(Player player, Runnable after) {
        story("HARI KE-40: SURAT DARI ALDRIC",
            "Di hari terakhir, sebuah surat tiba dari kota.\n" +
            "Tulisan tangan Aldric. Gemetar tapi jelas.\n\n" +
            "  \"Nak,\"\n" +
            "  \"Aku mendengar kabar dari desa.\"\n" +
            "  \"Kael sudah kembali. Naga sudah bebas.\"\n" +
            "  \"Dan tokonya... hiduplah lagi.\"\n\n" +
            "  \"Aku menghabiskan dua puluh tahun membawa rasa bersalah ini.\"\n" +
            "  \"Kamu mengangkatnya dariku hanya dalam empat puluh hari.\"\n\n" +
            "  \"Toko itu bukan hanya milikku lagi.\"\n" +
            "  \"Sekarang dia milikmu. Sepenuhnya.\"\n\n" +
            "  \"Jaga dia baik-baik. Jaga desa itu.\"\n" +
            "  \"Dan jangan pernah lupa mengapa kamu memilih bertahan.\"\n\n" +
            "                              — Aldric\n\n" +
            "( Reputasi +5 — penutup perjalananmu )",
            () -> {
                if (player != null) player.gainReputation(5);
                if (after != null) after.run();
            });
    }
}
