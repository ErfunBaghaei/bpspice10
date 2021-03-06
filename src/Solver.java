import java.util.ArrayList;

public class Solver {
    int [] [] sgraph ;
    double di,dv,dt,time=0,endtime;
    ArrayList<Union> sunions = new ArrayList();
    ArrayList<Element> selements=new ArrayList<Element>();
    ArrayList<Node> snodes= new ArrayList();
   Solver(ArrayList<Union> a, ArrayList<Element> b,ArrayList<Node> c, int [] [] d,double deltav,double deltat,double deltai,double time){
       int i,j;
       endtime=time;
       di= deltai;
       dv=deltav;
       dt= deltat;
       for (i=0;i<a.size();i++) sunions.set(i,a.get(i));
       for (i=0;i<b.size();i++) selements.set(i,b.get(i));
       for (i=0;i<c.size();i++) snodes.set(i,c.get(i));
       for (i=0;i<snodes.size();i++) for (j=0;j<snodes.size();j++) sgraph[i][j]= d[i][j];
   }
    void resetvoltage() {
        int i, flag, j,k,m;
        for (j = 0; j < sunions.size(); j++){
            sunions.get(j).nod.get(0).voltagedef = true;
        for (i = 1; i < sunions.get(j).nod.size(); i++) sunions.get(j).nod.get(i).voltagedef = false;
        while (true) {
            flag = 0;
            for (i = 0; i < sunions.get(j).nod.size(); i++) if (sunions.get(j).nod.get(i).voltagedef == false) flag = 1;
            if (flag == 0) break;
            for (i=0;i<sunions.get(j).nod.size();i++){
                if (sunions.get(j).nod.get(i).voltagedef==true){
                    for (k=0;k<sunions.get(j).nod.size();k++){
                        if (sunions.get(j).nod.get(k).voltagedef==false){
                           for (m=0;m<selements.size();m++){
                               if (selements.get(m).name.charAt(0)=='v'||selements.get(m).name.charAt(0)=='V'||selements.get(m).name.charAt(0)=='e'||selements.get(m).name.charAt(0)=='E'||selements.get(m).name.charAt(0)=='h'||selements.get(m).name.charAt(0)=='H'){
                                 if (selements.get(m).node1.equals(sunions.get(j).nod.get(i).name)&&selements.get(m).node2.equals(sunions.get(j).nod.get(k).name)){
                                     sunions.get(j).nod.get(k).voltagedef=true;
                                     sunions.get(j).nod.get(k).voltage=sunions.get(j).nod.get(i).voltage-selements.get(m).dc;
                                 }
                                   if (selements.get(m).node2.equals(sunions.get(j).nod.get(i).name)&&selements.get(m).node1.equals(sunions.get(j).nod.get(k).name)){
                                       sunions.get(j).nod.get(k).voltagedef=true;
                                       sunions.get(j).nod.get(k).voltage=sunions.get(j).nod.get(i).voltage+selements.get(m).dc;
                                   }
                               }
                           }
                        }
                    }
                }
            }
        }
    }
    }
    void mainsolver() {
        int i, j, k;
        double skcl = 0, skcl2 = 0;
        resetvoltage();
        Kcl();
        for (j = 0; j < sunions.size(); j++) skcl += sunions.get(j).kcl * sunions.get(j).kcl;
        skcl = Math.sqrt(skcl);
        for (i = 1; i <= endtime / dt; i++) {
            for (j = 1; j < sunions.size(); j++) {
                sunions.get(j).nod.get(0).voltage += dv;
                resetvoltage();
                Kcl();
                for (k = 0; k < sunions.size(); k++) skcl2 += sunions.get(k).kcl * sunions.get(k).kcl;
                skcl2 = Math.sqrt(skcl2);
                if (skcl2 < skcl) skcl = skcl2;
                else {
                    sunions.get(j).nod.get(0).voltage -= 2 * dv;
                    resetvoltage();
                    Kcl();
                    for (k = 0; k < sunions.size(); k++) skcl2 += sunions.get(k).kcl * sunions.get(k).kcl;
                    skcl2 = Math.sqrt(skcl2);
                    if (skcl2 < skcl) skcl = skcl2;
                    else sunions.get(j).nod.get(0).voltage += dv;
                    resetvoltage();
                }
            }
            time += dt;
        }
    }
    double findnode(String name){
       int i,j;
       double volt=0;
       for (i=0;i<sunions.size();i++){
           for (j=0;j<sunions.get(i).nod.size();j++){
               if (sunions.get(i).nod.get(j).name.equals(name)) volt= sunions.get(i).nod.get(j).voltage;
           }
       }
       return volt;
    }
    void Kcl(){
       int i,j,k;
       for (i=0;i<sunions.size();i++){
           for (j=0;j<sunions.get(i).nod.size();j++){
               for (k=0;k<selements.size();k++){
                   if (selements.get(k).name.charAt(0)=='r'||selements.get(k).name.charAt(0)=='R'){
                       if (selements.get(k).node1.equals(sunions.get(i).nod.get(j).name)) sunions.get(i).kcl+=(sunions.get(i).nod.get(j).voltage-findnode(selements.get(k).node2))/selements.get(k).resistance;
                       if (selements.get(k).node2.equals(sunions.get(i).nod.get(j).name)) sunions.get(i).kcl+=(sunions.get(i).nod.get(j).voltage-findnode(selements.get(k).node1))/selements.get(k).resistance;
                   }
                   if (selements.get(k).name.charAt(0)=='i'||selements.get(k).name.charAt(0)=='I'){
                       if (selements.get(k).node1.equals(sunions.get(i).nod.get(j).name)) sunions.get(i).kcl-=selements.get(k).dc;
                       if (selements.get(k).node2.equals(sunions.get(i).nod.get(j).name)) sunions.get(i).kcl+=selements.get(k).dc;
                   }
               }
           }
       }
    }
}
