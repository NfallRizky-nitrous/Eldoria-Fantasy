public void start() {
  system.out.print("Masukkan Nama : ");
  String name = input.nextLine();

  player = new Player(name);

  System.out.println("Hari pertama dimulai...");

  //event
  event.firstCustomer(player);

  int choice;

  do{
    System.out.println("1.LANJUT JAGA TOKO");
    System.out.println("2.EXIT");
    System.out.println("Pilih : ");

    choice = input.nextInt();
    if (choice == 1) {
        event.triggerEvent(player);
    }

  } while (choice != 0);
  
}
