package cl.landscape.controlcaudal;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendCellsRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.NumberFormat;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;

public class SheetsQuickstart {
    /** Application name. */
    private static final String APPLICATION_NAME =
        "Google Sheets API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/sheets.googleapis.com-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/sheets.googleapis.com-java-quickstart
     */
    private static final List<String> SCOPES =
        Arrays.asList(SheetsScopes.SPREADSHEETS);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
            SheetsQuickstart.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    public static Sheets getSheetsService() throws IOException {
        Credential credential = authorize();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main(String[] args) throws IOException {
        // Build a new authorized API client service.
    	createRequest();
    	
//        Sheets service = getSheetsService();
//        
//        // Prints the names and majors of students in a sample spreadsheet:
//        // https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
//        String spreadsheetId = "13CIIfI2uSxVWwqcSXkOdlHvEsSrZ18p_bgFXFz7IKus";
//        String range = "Class Data!A2:E";
//        
//        ValueRange response = service.spreadsheets().values().get(spreadsheetId, range)
//            .execute();
//        
//        List<List<Object>> values = response.getValues();
//        if (values == null || values.size() == 0) {
//            System.out.println("No data found.");
//        } else {
//          System.out.println("Name, Major");
//          for (List row : values) {
//            // Print columns A and E, which correspond to indices 0 and 4.
//            System.out.printf("%s, %s\n", row.get(0), row.get(4));
//          }
//        }
    }
    
    public static CellData createDateCell() {
    	CellData cell = new CellData();
    	cell.setUserEnteredValue(new ExtendedValue().setNumberValue(42198.0));
    	cell.setUserEnteredFormat(
    	    new CellFormat().setNumberFormat(new NumberFormat().setType("DATE")));
    	return cell;
    }
    
    public static CellData createDepthCell() {
    	CellData cell = new CellData();
    	cell.setUserEnteredValue(new ExtendedValue().setNumberValue(300.0));
    	return cell;
    }
    
    public static void createRequest() throws IOException {
    	List<RowData> rowDataList = new ArrayList<RowData>();
    	List<CellData> cellData = new ArrayList<CellData>();
    	cellData.add(createDateCell());
    	cellData.add(createDepthCell());
    	
    	RowData rowData = new RowData();
    	rowData.setValues(cellData);
    	
    	rowDataList.add(rowData);
    	
    	List<Request> requestList = new ArrayList<Request>();
    	Request request = new Request();
    	
    	AppendCellsRequest appendCellReq = new AppendCellsRequest();
        appendCellReq.setSheetId(0);
        appendCellReq.setRows(rowDataList);           
        appendCellReq.setFields("userEnteredValue,userEnteredFormat.numberFormat");
        
        request.setAppendCells(appendCellReq);
        requestList.add(request);
        
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest();
        batchUpdateRequest.setRequests(requestList);
        
        getSheetsService().spreadsheets().batchUpdate("13CIIfI2uSxVWwqcSXkOdlHvEsSrZ18p_bgFXFz7IKus", batchUpdateRequest).execute();
        
    }


}