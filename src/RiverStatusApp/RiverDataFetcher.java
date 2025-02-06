package RiverStatusApp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RiverDataFetcher {
    private static final Logger logger = LogManager.getLogger(RiverDataFetcher.class);

	public static String capitalizeWords(String input) {
	    String[] words = input.split(" ");
	    StringBuilder result = new StringBuilder();
	    for (String word : words) {
	        String correctedWord = word.substring(0, 1).toUpperCase();
	        for (int i = 1; i < word.length(); i++) {
	            if (Character.isUpperCase(word.charAt(i))) {
	                correctedWord += Character.toLowerCase(word.charAt(i));
	            } else {
	                correctedWord += word.charAt(i);
	            }
	        }
	        result.append(correctedWord).append(" ");
	    }
	    return result.toString().trim();
	}
	
    public String[][] fetchData() {
    	
    	String[] url2 = {
    			"https://komunikaty.tvp.pl/komunikaty/podlaskie/stany-wod",
    			"https://komunikaty.tvp.pl/komunikaty/dolnoslaskie/stany-wod",
    			"https://komunikaty.tvp.pl/komunikaty/kujawsko-pomorskie/stany-wod",
    			"https://komunikaty.tvp.pl/komunikaty/lubelskie/stany-wod",
    			"https://komunikaty.tvp.pl/komunikaty/lubuskie/stany-wod",
    			"https://komunikaty.tvp.pl/komunikaty/lodzkie/stany-wod",
    			"https://komunikaty.tvp.pl/komunikaty/malopolskie/stany-wod",
    			"https://komunikaty.tvp.pl/komunikaty/mazowieckie/stany-wod",
    			"https://komunikaty.tvp.pl/komunikaty/opolskie/stany-wod",
    			"https://komunikaty.tvp.pl/komunikaty/podkarpackie/stany-wod",
    			"https://komunikaty.tvp.pl/komunikaty/podlaskie/stany-wod",
    			"https://komunikaty.tvp.pl/komunikaty/pomorskie/stany-wod",
    			"https://komunikaty.tvp.pl/komunikaty/slaskie/stany-wod",
    			"https://komunikaty.tvp.pl/komunikaty/swietokrzyskie/stany-wod",
    			"https://komunikaty.tvp.pl/komunikaty/warminsko-mazurskie/stany-wod",
    			"https://komunikaty.tvp.pl/komunikaty/wielkopolskie/stany-wod",
    			"https://komunikaty.tvp.pl/komunikaty/zachodniopomorskie/stany-wod"
    			};
		String[][] tvp_podlaskie = new String[600][7];
		
    	try {
    		logger.info("Fetching data from komunikaty.tvp.pl");
			int tr = 0;
			int td = 0;
			for(int i = 0; i < url2.length; i++) {
				Document document2 = Jsoup.connect(url2[i]).get();
				logger.debug("HTML Document: " + document2.html());

				for (Element row : document2.select("table.table.table-striped tr")) {
					if(row.select("td:nth-of-type(1)").text().equals("")) {
						continue;
					}else {
						tvp_podlaskie[tr][td] = capitalizeWords(row.select("td:nth-of-type(1)").text());	//rzeka
						td++;
						tvp_podlaskie[tr][td] = capitalizeWords(row.select("td:nth-of-type(2)").text());	//wodowskaz
						td++;
						tvp_podlaskie[tr][td] = row.select("td:nth-of-type(3)").text();	//stan_aktualny
						td++;
						tvp_podlaskie[tr][td] = row.select("td:nth-of-type(4)").text();	//trend
						td++;
						tvp_podlaskie[tr][td] = row.select("td:nth-of-type(5)").text();	//stan_ostrzegawczy
						td++;
						tvp_podlaskie[tr][td] = row.select("td:nth-of-type(6)").text();	//stan_aktualny
						td++;
						tvp_podlaskie[tr][td] = row.select("td:nth-of-type(7)").text();	//ostatnia_aktualizacja
						
						//System.out.println(rzeka+" || "+wodowskaz+ " || " +stan_aktualny+" || "+stan_ostrzegawczy+" || "+stan_alarmowy+" || "+ostatnia_aktualizacja);
						tr++;
						td = 0;
					}
				}
			}
			logger.info("Number of records from komunikaty.tvp.pl: " + tr);
		}
		catch(IOException e) {
			logger.info("ERROR in updating data komunikaty.tvp.pl");
			e.printStackTrace();
		}
    	return tvp_podlaskie;
    }

    public String[][] fetchDataWale(){
    	logger.info("Fetching data from sepa.org.uk");
		String url3 = "https://www2.sepa.org.uk/waterlevels/";
        String[][] rzeki_anglia = new String[30][5];
        
        try {
            Document document3 = Jsoup.connect(url3).get();
            System.out.println(document3.text());
            int tr = 0;
            int td = 0;
            for(Element row : document3.select("table.searchResultsGrid tr")) {
                if(row.select("td:nth-of-type(1)").text().equals("")) {
                    continue;
                }else {
                    rzeki_anglia[tr][td] = row.select("td:nth-of-type(1)").text();
                    td++;
                    rzeki_anglia[tr][td] = row.select("td:nth-of-type(2)").text();
                    td++;
                    rzeki_anglia[tr][td] = row.select("td:nth-of-type(3)").text();
                    td++;
                    rzeki_anglia[tr][td] = row.select("td:nth-of-type(4)").text();
                    td++;
                    rzeki_anglia[tr][td] = row.select("td:nth-of-type(5)").text();

                    //System.out.println(rzeki_anglia[tr][0] + " " + rzeki_anglia[tr][1] + " " + rzeki_anglia[tr][2] + " " + rzeki_anglia[tr][3] + " " + rzeki_anglia[tr][4]);

                    if(tr == 19) {
                        break;
                    }
                    tr++;
                    td = 0;
                }
            }
            logger.info("Number of records from sepa.org.uk: " + tr);
        }catch(IOException e) {
        	logger.info("ERROR in updating data from sepa.org.uk");
            e.printStackTrace();
        }
        
        return rzeki_anglia;
    }
    
    public static void main(String[] args) {
        RiverDataFetcher fetcher = new RiverDataFetcher();
        String[][] data = fetcher.fetchData();
        //for (RiverData rd : data) {
        //    System.out.println(rd);
        //}
    }
}

