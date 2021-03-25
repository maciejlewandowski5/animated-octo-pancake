package modelv2;

import java.util.ArrayList;

public class Debt {

    String from;
    String to;
    float amount;

    public Debt(String fromUserId, String toUserId, float amount) {
        this.from = fromUserId;
        this.to = toUserId;
        this.amount = amount;
    }

    public Debt(Debt debt1, Debt debt2) throws InstantiationException {
        if (debt1.from.equals(debt2.from)) {
            if (debt1.to.equals(debt2.to)) {  // debts are with the same form to
                this.from = debt1.from;
                this.to = debt1.to;
                this.amount = debt1.amount + debt2.amount;
            } else {
                throw new InstantiationException("debt1 to different then debt2 to, use merge method");
            }
        } else if (debt1.from.equals(debt2.to)) {
            if (debt1.to.equals(debt2.from)) { // debts are with opposite from and
                if (debt1.amount > debt2.amount) {
                    this.from = debt2.from;
                    this.to = debt2.to;
                    this.amount = debt1.amount - debt2.amount;
                } else {
                    this.from = debt1.from;
                    this.to = debt1.to;
                    this.amount = debt2.amount - debt2.amount;
                }
            } else {
                throw new InstantiationException("debt1 to different then debt 2 from, use merge method");
            }
        } else if (debt1.to.equals(debt2.from)) {
            throw new InstantiationException("debt1 from different then debt2 to, use merge method");
        } else {
            throw new InstantiationException("debt1 from different then debt2 from, use merge method");
        }
    }

    public static ArrayList<Debt> mergeDebts(Debt debt1, Debt debt2) throws InstantiationException {
        ArrayList<Debt> result = new ArrayList<>();
        Debt tmp1 = null;
        Debt tmp2 = null;

        if(debt1.from.equals(debt2.to) && debt1.to.equals(debt2.from)){
            throw new InstantiationException("use contnructor Debt(Debt,Debt)");
        }
        else if (debt1.from.equals(debt2.to) && !debt1.to.equals(debt2.from)) {
            if (debt1.amount > debt2.amount) {
                 tmp1 = new Debt(debt2.from, debt1.to, debt2.amount);
                 tmp2 = new Debt(debt1.from, debt1.to, debt1.amount - debt2.amount);
            } else if (debt1.amount <= debt2.amount) {
                 tmp1 = new Debt(debt1.from, debt2.to, debt1.amount);
                 tmp2 = new Debt(debt2.from, debt2.to, debt2.amount - debt1.amount);
            }

        }
        else {
            if (debt1.amount > debt2.amount) {
                 tmp1 = new Debt(debt2.from, debt1.to, debt2.amount);
                 tmp2 = new Debt(debt1.from, debt1.to, debt1.amount - debt2.amount);
            } else if (debt1.amount <= debt2.amount) {
                 tmp1 = new Debt(debt1.from, debt2.to, debt1.amount);
                 tmp2 = new Debt(debt2.from, debt2.to, debt2.amount - debt1.amount);
            }

        }

        result.add(tmp1);
        result.add(tmp2);
        return result;
    }


    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public float getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Debt{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", amount=" + amount +
                '}';
    }
}
