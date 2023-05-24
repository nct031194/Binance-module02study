package TradeService;

import Crypto.Crypto;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.UUID;

import static Account.AccService.AccountService.now;
import static Account.Account.getAccountlogged;
import static Account.Wallet.Wallet.getWallet;
import static Account.Wallet.WalletService.walletToFile;
import static Market.MarketMenu.timer3s;
import static Menu.LoginMain.User.UserMain.usermain;
import static Menu.WelcomePage.*;
import static TradeService.BuyCryptoService.*;

public class BuyOrder {
    private static Scanner sc = new Scanner(System.in);
    static void buyOrderUser(Crypto crypto, Crypto crypto2) throws IOException {
        timer3s.cancel();
        System.out.println("Enter Price :");
        while(!sc.hasNextDouble()){
            System.out.println("invalid input");
            sc.next();
        }
        double inputPrice = sc.nextDouble();
        System.out.println("Enter Amount " + buyAllinfo(crypto,crypto2,inputPrice) + " :");
        while(!sc.hasNextDouble()){
            System.out.println("invalid input");
            sc.next();
        }
        double inputAmount = sc.nextDouble();
        if(inputAmount == 0) buyCrypto(crypto,crypto2);
        if (getWallet().get(crypto2)/inputPrice <= inputAmount){
            System.out.println(red + "NOT ENOUGH " + crypto2.getCrypto() + RESET);
            buyOrderUser(crypto,crypto2);
        }
        else{
            addBuyOrderUser(crypto,crypto2,inputPrice,inputAmount);
            double oldAmount = getWallet().get(crypto2);
            getWallet().put(crypto2, oldAmount - (inputAmount*inputPrice));
            walletToFile(getAccountlogged().getUsername());
            System.out.println(GREEN + "SUCCESS ! You have order buy limit " + YELLOW + inputAmount + crypto.getCrypto() + GREEN + " for " + YELLOW  + inputAmount*inputPrice + crypto2.getCrypto() + GREEN + " at price " + BLUE + inputPrice + RESET);
            System.out.println("press any key to continue");
            if (sc.hasNext()){
                System.out.println(" ");
                usermain();
            }
        }
    }
    static void addBuyOrderUser(Crypto crypto, Crypto crypto2, double price , double amount){
        UUID newID = UUID.randomUUID();
        now = LocalDateTime.now();
        try {
            String path = "Data/User/" + getAccountlogged().getUsername();
            FileWriter fileWriter = new FileWriter(path + "/buysellhistory.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("pending,buy," + now + "," + crypto.getCrypto() + "," + crypto2.getCrypto() + "," + price + "," + amount + "," + newID + "\n");
            bufferedWriter.close();
            fileWriter.close();

            path = "Data/";
            FileWriter fileWriter1 = new FileWriter(path + "buysellorders.txt",true);
            BufferedWriter bufferedWriter1 = new BufferedWriter(fileWriter1);
            bufferedWriter1.write("buy," + now + "," + crypto.getCrypto() + "," + crypto2.getCrypto() + "," +  price + "," + amount + "," + getAccountlogged().getUsername() + "," + newID + "\n");
            bufferedWriter1.close();
            fileWriter1.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
