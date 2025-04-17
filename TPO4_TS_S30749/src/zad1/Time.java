/**
 *
 *  @author Trauth Szymon  S30749
 *
 */

package zad1;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;


public class Time {
    public static String passed(String s1, String s2) {

        String result = "";
        Locale locale = new Locale("pl", "PL");
        if (s1.contains("T"))
        {
            try
            {
                // błąd parsowania można łatwo naprawić 2 linijkami:
//            s1=s1.replaceAll("T:", "T");
//            s2=s2.replaceAll("T:", "T");
                LocalDateTime date1 = LocalDateTime.parse(s1);
                LocalDateTime date2 = LocalDateTime.parse(s2);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy (EEEE) 'godz.' HH:mm", locale);
                String formattedDate1 = date1.format(formatter);
                String formattedDate2 = date2.format(formatter);
                String od_do = "Od "+formattedDate1+" do "+formattedDate2;

                long dni = (long) Math.ceil((double) (ChronoUnit.HOURS.between(date1, date2)) /24);
                String tygodnie = String.format("%.2f", (double)dni/7);
                tygodnie=tygodnie.replace(',','.');
                String mija = (dni == 1) ? "\n - mija: 1 dzień, tygodni " + tygodnie :
                        (dni > 1) ? "\n - mija: " + dni + " dni, tygodni " + tygodnie :
                                "\n - mija: 0 dni";

                ZoneId zoneId = ZoneId.of("Europe/Warsaw");
                ZonedDateTime zdt1 = ZonedDateTime.of(date1, zoneId);
                ZonedDateTime zdt2 = ZonedDateTime.of(date2, zoneId);
                long godziny = ChronoUnit.HOURS.between(zdt1, zdt2);
                long minuty = ChronoUnit.MINUTES.between(zdt1, zdt2);
                String godzin="\n - godzin: "+godziny+", minut: "+minuty;

                LocalDate data1 = date1.toLocalDate();
                LocalDate data2 = date2.toLocalDate();
                Period period = Period.between(data1, data2);
                String kalendarzowo="";
                String lataStr = (period.getYears() == 1) ? period.getYears() + " rok" : (period.getYears() > 1) ? period.getYears() + " lata" : "";
                String miesiaceStr = (period.getMonths() == 1) ? period.getMonths() + " miesiąc" : (period.getMonths() > 1) ? period.getMonths() + " miesięcy" : "";
                String dniStr = (period.getDays() == 1) ? period.getDays() + " dzień" : (period.getDays() > 1) ? period.getDays() + " dni" : "";

                if (!lataStr.isEmpty()) {
                    kalendarzowo += lataStr;
                }
                if (!miesiaceStr.isEmpty()) {
                    if (!kalendarzowo.isEmpty()) {
                        kalendarzowo += ", ";
                    }
                    kalendarzowo += miesiaceStr;
                }
                if (!dniStr.isEmpty()) {
                    if (!kalendarzowo.isEmpty()) {
                        kalendarzowo += ", ";
                    }
                    kalendarzowo += dniStr;
                }
                kalendarzowo = "\n - kalendarzowo: " + kalendarzowo;


                result = od_do+mija+godzin+kalendarzowo;
            }
            catch(DateTimeParseException e)
            {
                return "*** "+e.getClass().getName()+": "+e.getMessage();
            }

        }
        else
        {
            try
            {
                LocalDate date1 = LocalDate.parse(s1);
                LocalDate date2 = LocalDate.parse(s2);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy (EEEE)", locale);
                String formattedDate1 = date1.format(formatter);
                String formattedDate2 = date2.format(formatter);

                String od_do = "Od "+formattedDate1+" do "+formattedDate2;

                long dni = ChronoUnit.DAYS.between(date1, date2);
                String tygodnie = String.format("%.2f", (double)dni/7);
                tygodnie=tygodnie.replace(',','.');
                String mija = (dni == 1) ? "\n - mija: 1 dzień, tygodni " + tygodnie :
                        (dni > 1) ? "\n - mija: " + dni + " dni, tygodni " + tygodnie :
                                "\n - mija: 0 dni";

                Period period = Period.between(date1, date2);
                String kalendarzowo="";
                String lataStr = (period.getYears() == 1) ? period.getYears() + " rok" : (period.getYears() > 1 && period.getYears() < 5) ? period.getYears() + " lata" : (period.getYears() > 5) ? period.getYears() + " lat": "";
                String miesiaceStr = (period.getMonths() == 1) ? period.getMonths() + " miesiąc" : (period.getMonths() > 1 && period.getMonths() < 5) ? period.getMonths() + " miesiące" : (period.getMonths() > 5) ? period.getMonths() + " miesięcy": "";
                String dniStr = (period.getDays() == 1) ? period.getDays() + " dzień" : (period.getDays() > 1) ? period.getDays() + " dni" : "";

                if (!lataStr.isEmpty()) {
                    kalendarzowo += lataStr;
                }
                if (!miesiaceStr.isEmpty()) {
                    if (!kalendarzowo.isEmpty()) {
                        kalendarzowo += ", ";
                    }
                    kalendarzowo += miesiaceStr;
                }
                if (!dniStr.isEmpty()) {
                    if (!kalendarzowo.isEmpty()) {
                        kalendarzowo += ", ";
                    }
                    kalendarzowo += dniStr;
                }
                kalendarzowo = "\n - kalendarzowo: " + kalendarzowo;


                result = od_do+mija+kalendarzowo;

            }
            catch(DateTimeParseException e)
            {
                return "*** "+e.getClass().getName()+": "+e.getMessage();
            }

        }


        return result;

    }
}
