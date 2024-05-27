package Components;

import java.util.Date;

public class Reservation {
    private String email;
    private Date checkInDate;
    private Date checkOutDate;
    private String QRname;
    private String ReceiptName;
    private Double price;

    public Reservation(String e, Date in, Date out, String QR, String r, Double p){
        email = e;
        checkInDate = in;
        checkOutDate = out;
        QRname = QR;
        ReceiptName = r;
        price = p;
    }

    public String getEmail(){
        return email;
    }
    public String getQRname(){
        return QRname;
    }
    public String getReceiptName(){
        return ReceiptName;
    }
    public Date getCheckInDate(){
        return checkInDate;
    }
    public Date getCheckOutDate(){
        return checkOutDate;
    }
    public Double getPrice(){
        return price;
    }

}
