/**
 * 
 *
 * @author Alec LaLonde
 */
public class UnitTester {

    /**
     * 
     */
    public UnitTester()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args)
    {
        String abcNote1 = "B";
        String abcNote2 = "c";
        String abcNote3 = "c'";
        String abcNote4 = "C";
        String abcNote5 = "_f";
        String abcNote6 = "^G";
        
        String HGNote1 = "B5";
        String HGNote2 = "D6";
        String HGNote3 = "G4";
        String HGNote4 = "F#3";
        String HGNote5 = "Bb4";
        String HGNote6 = "C5";
        
        String converted1 = Utilities.convertABCtoHG(abcNote1);
        String converted2 = Utilities.convertABCtoHG(abcNote2);
        String converted3 = Utilities.convertABCtoHG(abcNote3);
        String converted4 = Utilities.convertABCtoHG(abcNote4);
        String converted5 = Utilities.convertABCtoHG(abcNote5);
        String converted6 = Utilities.convertABCtoHG(abcNote6);
        
        String converted7 = Utilities.convertHGtoABC(HGNote1, true);
        String converted8 = Utilities.convertHGtoABC(HGNote2, true);
        String converted9 = Utilities.convertHGtoABC(HGNote3, true);
        String converted10 = Utilities.convertHGtoABC(HGNote4, false);
        String converted11 = Utilities.convertHGtoABC(HGNote5, false);
        String converted12 = Utilities.convertHGtoABC(HGNote6, false);  
        
        System.out.println(abcNote1 + " has become " + converted1);
        System.out.println(abcNote2 + " has become " + converted2);
        System.out.println(abcNote3 + " has become " + converted3);
        System.out.println(abcNote4 + " has become " + converted4);
        System.out.println(abcNote5 + " has become " + converted5);
        System.out.println(abcNote6 + " has become " + converted6);
        System.out.println();
        System.out.println(HGNote1 + " has become " + converted7);
        System.out.println(HGNote2 + " has become " + converted8);
        System.out.println(HGNote3 + " has become " + converted9);
        System.out.println(HGNote4 + " has become " + converted10);
        System.out.println(HGNote5 + " has become " + converted11);
        System.out.println(HGNote6 + " has become " + converted12);
    }
}
