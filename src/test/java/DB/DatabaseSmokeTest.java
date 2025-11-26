package DB;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Common;
import utils.DBHelper;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class DatabaseSmokeTest {
	private DBHelper db;
    public static Map<String, Object> EXPORTED_VALUES = new HashMap<>();

    // Helper class to store query information
	private static class QueryInfo {
		String query;
		String variableName;
		String resultColumn;
		ExtractionType extractionType;
		
		QueryInfo(String query, String variableName, String resultColumn, ExtractionType extractionType) {
			this.query = query;
			this.variableName = variableName;
			this.resultColumn = resultColumn;
			this.extractionType = extractionType;
		}
	}
	
	// Enum to define different extraction types
	private enum ExtractionType {
		COUNT_INT,      // SELECT COUNT(*) - get int from result column
		DOUBLE_VALUE,  // SELECT SUM/AVG/MAX/MIN - get double from result column
		COUNT_ROWS,// SELECT * - count all rows with while loop
		COUNT_EXISTS   // SELECT * - check if exists (count = 1 if exists, 0 otherwise)
	}

	@BeforeClass
	public void setUp() {
        String url = System.getProperty("db.url",
                "jdbc:mysql://development.credebt.com:3306/Credebt_Machine");
		String user = System.getProperty("db.user", "credebt-read");
		String pass = System.getProperty("db.pass", "xT2#l9V8*G");

		if (url == null || url.trim().isEmpty()) {
			throw new IllegalArgumentException("db.url is required. Provide -Ddb.url=jdbc:mysql://host:3306/dbname");
		}
		if (user == null) user = "";
		if (pass == null) pass = "";

        Common.log("Connecting to DB: " + url);
		db = new DBHelper(url, user, pass);
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() {
        writeJsonToFile();
		// No persistent connection kept; nothing to close here.
	}
    private void writeJsonToFile() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // you can write to src/test/resources or target directory
            File output = new File("StoredData/db_values.json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(output, EXPORTED_VALUES);
            Common.log("DB values exported to JSON: " + output.getAbsolutePath());
        } catch (Exception e) {
            Common.error("Failed to write JSON file: " + e.getMessage());
        }
    }
	// Helper method to execute a query and extract the result
	private void executeQuery(QueryInfo queryInfo) {
		try {
			ResultSet rs = db.executeQuery(queryInfo.query);
			Object result = null;
			
			switch (queryInfo.extractionType) {
				case COUNT_INT:
					int count = 0;
					if (rs.next()) {
						count = rs.getInt(queryInfo.resultColumn);
					}
					result = count;
					break;


				case DOUBLE_VALUE:
                    BigDecimal bd = BigDecimal.ZERO;
                    if (rs.next()) {
                        bd = rs.getBigDecimal(queryInfo.resultColumn);
                    }
                    result = bd;
                    break;
					
				case COUNT_ROWS:
					int rowCount = 0;
					while (rs.next()) {
						rowCount++;
					}
					result = rowCount;
					break;
					
				case COUNT_EXISTS:
					int exists = 0;
					if (rs.next()) {
						exists = 1;
					}
					result = exists;
					break;
			}

            EXPORTED_VALUES.put(queryInfo.variableName, result);
			// Store result in a variable (using reflection or Map for simplicity)
            Common.log(queryInfo.variableName + " : " + result);
			
		} catch (Exception e) {
            Common.error("---------------------------------------------");
            Common.error("Error executing query: " + queryInfo.variableName + " " + e.getMessage());
            Common.error("Query: " + queryInfo.query);
		}
	}

	@Test
	public void db_Value_Extraction() throws Exception {
		// Define all queries in arrays
		QueryInfo[] queries = {
			// Special case: existence check
			new QueryInfo("SELECT * FROM closing_position WHERE usd=0.8508", "DB_GET_CLOSING_USD_8508", null, ExtractionType.COUNT_EXISTS),
			// Investment table queries
			new QueryInfo("SELECT SUM(face_value) as val FROM investment", "DB_GET_SUM_INVESTMENT_FACE_VALUE", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT SUM(months) as total_months FROM investment WHERE deleted_at IS NULL", "DB_GET_CLOSING_POSITION_USD_RECORDS", "total_months", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT SUM(buy_rate) as total_amount FROM investment WHERE deleted_at IS NULL", "DB_GET_SUM_BUY_RATE", "total_amount", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT SUM(payment_out) as total_amount FROM investment WHERE deleted_at IS NULL", "DB_GET_SUM_PAYMENT_OUT", "total_amount", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT AVG(face_value) as val FROM investment WHERE deleted_at IS NULL", "DB_GET_AVG_INVESMENT_FACE_VALUE", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT AVG(months) as val FROM investment", "DB_GET_AVG_INVESMENT_MONTHS", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT AVG(payment_out) as val FROM investment WHERE deleted_at IS NULL", "DB_GET_AVG_PAYMENT_OUT", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT AVG(receipt_in) as val FROM investment WHERE deleted_at IS NULL", "DB_GET_AVG_RECEIPT_IN", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(face_value) as val FROM investment WHERE deleted_at IS NULL", "DB_GET_MAX_INVESTMENT_FACE_VALUE", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(months) as val FROM investment WHERE deleted_at IS NULL", "DB_GET_MAX_MONTHS", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(buy_rate) as val FROM investment WHERE deleted_at IS NULL", "DB_GET_MAX_INVESTMENT_BUY_RATE", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(payment_out) as val FROM investment WHERE deleted_at IS NULL", "DB_GET_MAX_PAYMENT_OUT", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(receipt_in) as val FROM investment WHERE deleted_at IS NULL", "DB_GET_MAX_RECEIPT_IN", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MIN(face_value) as val FROM investment WHERE deleted_at IS NULL", "DB_GET_MIN_INVESTMENT_FACE_VALUE", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MIN(months) as val FROM investment WHERE deleted_at IS NULL", "DB_GET_MIN_MONTHS", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MIN(buy_rate) as val FROM investment WHERE deleted_at IS NULL", "DB_GET_MIN_INVESTMENT_BUY_RATE", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MIN(payment_out) as val FROM investment WHERE deleted_at IS NULL", "DB_GET_MIN_PAYMENT_OUT", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MIN(receipt_in) as val FROM investment WHERE deleted_at IS NULL", "DB_GET_MIN_RECEIPT_IN", "val", ExtractionType.DOUBLE_VALUE),

			// Asset table queries
			new QueryInfo("SELECT SUM(unit_price) as val FROM Entities.asset WHERE deleted_at IS NULL", "DB_GET_SUM_UNIT_PRICE", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT SUM(eot) as val FROM Entities.asset WHERE deleted_at IS NULL", "DB_GET_SUM_EOT", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT AVG(unit_price) as val FROM Entities.asset WHERE deleted_at IS NULL", "DB_GET_AVG_UNIT_PRICE", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT AVG(eot) as val FROM Entities.asset WHERE deleted_at IS NULL", "DB_GET_AVG_EOT", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(unit_price) as val FROM Entities.asset WHERE deleted_at IS NULL", "DB_GET_MAX_UNIT_PRICE", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(eot) as val FROM Entities.asset WHERE deleted_at IS NULL", "DB_GET_MAX_EOT", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MIN(eot) as val FROM Entities.asset WHERE deleted_at IS NULL", "DB_GET_MIN_EOT", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MIN(unit_price) as val FROM Entities.asset WHERE deleted_at IS NULL", "DB_GET_MIN_UNIT_PRICE", "val", ExtractionType.DOUBLE_VALUE),
			// Cash table queries
			new QueryInfo("SELECT SUM(payment_amount) as val FROM cash WHERE deleted_at IS NULL", "DB_GET_SUM_PAYMENT_AMOUNT", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT SUM(receipt_amount) as val FROM cash WHERE deleted_at IS NULL", "DB_GET_SUM_RECEIPT_AMOUNT", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT COUNT(*) AS val FROM cash WHERE deleted_at IS NULL", "DB_GET_ALL_CASH_RECORDS", "val", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT AVG(payment_amount) as val FROM cash WHERE deleted_at IS NULL", "DB_GET_AVG_PAYMENT_AMOUNT", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT AVG(receipt_amount) as val FROM cash WHERE deleted_at IS NULL", "DB_GET_AVG_RECEIPT_AMOUNT", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(payment_amount) as val FROM cash WHERE deleted_at IS NULL", "DB_GET_MAX_PAYMENT_AMOUNT", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(receipt_amount) as val FROM cash WHERE deleted_at IS NULL", "DB_GET_MAX_RECEIPT_AMOUNT", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MIN(receipt_amount) as val FROM cash WHERE deleted_at IS NULL", "DB_GET_MIN_RECEIPT_AMOUNT", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MIN(payment_amount) as val FROM cash WHERE deleted_at IS NULL", "DB_GET_MIN_PAYMENT_AMOUNT", "val", ExtractionType.DOUBLE_VALUE),

			// Closing position table queries - AVG
			new QueryInfo("SELECT AVG(demand) as val FROM closing_position", "DB_GET_AVG_DEMAND", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT AVG(fixed) as val FROM closing_position", "DB_GET_AVG_FIXED", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT AVG(term) as val FROM closing_position", "DB_GET_AVG_TERM", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT AVG(buy_rate) as val FROM closing_position", "DB_GET_AVG_BUY_RATE", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT AVG(gbp) as val FROM closing_position", "DB_GET_AVG_GBP", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT AVG(usd) as val FROM closing_position", "DB_GET_AVG_USD", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT AVG(bank_eur) as val FROM closing_position", "DB_GET_AVG_BANK_EUR", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT AVG(bank_usd) as val FROM closing_position", "DB_GET_AVG_BANK_USD", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT AVG(bank_gbp) as val FROM closing_position", "DB_GET_AVG_BANK_GBP", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT AVG(bank_eur_total) as val FROM closing_position", "DB_GET_AVG_BANK_EUR_TOTAL", "val", ExtractionType.DOUBLE_VALUE),

			// Closing position table queries - MAX
			new QueryInfo("SELECT MAX(demand) as val FROM closing_position", "DB_GET_MAX_DEMAND", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(fixed) as val FROM closing_position", "DB_GET_MAX_FIXED", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(term) as val FROM closing_position", "DB_GET_MAX_TERM", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(buy_rate) as val FROM closing_position", "DB_GET_MAX_CLOSING_BUY_RATE", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(gbp) as val FROM closing_position", "DB_GET_MAX_GBP", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(usd) as val FROM closing_position", "DB_GET_MAX_USD", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(bank_eur) as val FROM closing_position", "DB_GET_MAX_BANK_EUR", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(bank_usd) as val FROM closing_position", "DB_GET_MAX_BANK_USD", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(bank_gbp) as val FROM closing_position", "DB_GET_MAX_BANK_GBP", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(bank_eur_total) as val FROM closing_position", "DB_GET_MAX_BANK_EUR_TOTAL", "val", ExtractionType.DOUBLE_VALUE),

			// Closing position table queries - MIN
			new QueryInfo("SELECT MIN(demand) as val FROM closing_position", "DB_GET_MIN_DEMAND", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MIN(fixed) as val FROM closing_position", "DB_GET_MIN_FIXED", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MIN(term) as val FROM closing_position", "DB_GET_MIN_TERM", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MIN(buy_rate) as val FROM closing_position", "DB_GET_MIN_CLOSING_BUY_RATE", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MIN(gbp) as val FROM closing_position", "DB_GET_MIN_GBP", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MIN(usd) as val FROM closing_position", "DB_GET_MIN_USD", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MIN(bank_eur) as val FROM closing_position", "DB_GET_MIN_BANK_EUR", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MIN(bank_usd) as val FROM closing_position", "DB_GET_MIN_BANK_USD", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MIN(bank_gbp) as val FROM closing_position", "DB_GET_MIN_BANK_GBP", "val", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MIN(bank_eur_total) as val FROM closing_position", "DB_GET_MIN_BANK_EUR_TOTAL", "val", ExtractionType.DOUBLE_VALUE),

			// Buy table queries
			new QueryInfo("SELECT COUNT(*) as cnt FROM buy WHERE status='REJECTED'", "DB_GET_REJECTED_BUY_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM buy WHERE status='ASSESSED'", "DB_GET_ASSESSED_BUY_RECORDS", "cnt", ExtractionType.COUNT_INT),

			// Ledger posting queries
			new QueryInfo("SELECT COUNT(*) as cnt FROM ledger_posting WHERE type='d-ETR'", "DB_GET_LEDGER_POSTING_D_ETR", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM ledger_posting WHERE type='a-ETR'", "DB_GET_LEDGER_POSTING_A_ETR", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM ledger_posting WHERE type='c-ETR'", "DB_GET_LEDGER_POSTING_C_ETR", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM ledger_posting WHERE type='d-ETR/b-ETR'", "DB_GET_LEDGER_POSTING_D_OR_B_ETR", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM ledger_posting WHERE type='f-ETR'", "DB_GET_LEDGER_POSTING_F_ETR", "cnt", ExtractionType.COUNT_INT),

			// Model table queries
			new QueryInfo("SELECT COUNT(*) as cnt FROM model WHERE etr_type='Investment'", "DB_GET_MODEL_INVESTMENT", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM model WHERE etr_type='a-ETR'", "DB_GET_MODEL_A_ETR", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM model WHERE etr_type='b-ETR'", "DB_GET_MODEL_B_ETR", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM model WHERE etr_type='c-ETR'", "DB_GET_MODEL_C_ETR", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM model WHERE etr_type='d-ETR'", "DB_GET_MODEL_D_ETR", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM model WHERE etr_type='f-ETR'", "DB_GET_MODEL_F_ETR", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM model WHERE name='Receipt Fixed' AND ccy='EUR'", "DB_GET_MODEL_RECEIPT_FIXED_EUR", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM model WHERE etr_type='b-ETR' AND model_type='b-ETR'", "DB_GET_MODEL_B_ETR_TYPE", "cnt", ExtractionType.COUNT_INT),

			// CFX result queries
			new QueryInfo("SELECT SUM(cfx_payment_gain) as cnt FROM cfx_result", "DB_GET_SUM_CFX_PAYMENT_GAIN", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT SUM(cfx_receipt_gain) as cnt FROM cfx_result", "DB_GET_SUM_CFX_RECEIPT_GAIN", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT SUM(translation_fx) as cnt FROM cfx_result", "DB_GET_SUM_TRANSLATION_FX", "cnt", ExtractionType.COUNT_INT),

			//Trade->Transaction ETR table queries
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE deleted_at IS NULL AND ref_id IS NULL", "DB_GET_ALL_ETR_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type = 'a-etr' AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_A_ETR_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type = 'b-etr' AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_B_ETR_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type = 'c-etr' AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_C_ETR_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type = 'd-etr' AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_D_ETR_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type = 'f-etr' AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_F_ETR_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type = 'Credit Note' AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_CREDIT_NOTE_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type = 'Deduction' AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_DEDUCTION_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type = 'Deposit' AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_DEPOSIT_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type = 'ICP' AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_ICP_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type = 'OCPA' AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_OCPA_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type = 'IDP' AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_IDP_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type = 'ORP' AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_ORP_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type = 'ORR' AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_ORR_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type = 'RSA' AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_RSA_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type = 'OTR' AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_OTR_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type = 'OPAC' AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_OPAC_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type LIKE '%-ETR' AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_ALL_ETR_LIKE_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type IN ('a-etr', 'f-etr') AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_ASSET_TYPE_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type IN ('b-etr', 'c-etr', 'd-etr', 'd-etr/b-etr') AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_TRADE_TYPE_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type IN ('Credit Note', 'Deduction') AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_DEBITS_TYPE_RECORDS", "cnt", ExtractionType.COUNT_INT),
			new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type IN ('Deposit', 'OCPA', 'ICP') AND deleted_at IS NULL AND ref_id IS NULL", "DB_GET_CREDITS_TYPE_RECORDS", "cnt", ExtractionType.COUNT_INT),
            new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type = 'a-etr' AND deleted_at IS NULL AND ref_id IS NULL", "DB_A_ETR_AMORTISATION", "cnt", ExtractionType.COUNT_INT),
            new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type = 'f-etr' AND deleted_at IS NULL AND ref_id IS NULL", "DB_F_ETR_AMORTISATION", "cnt", ExtractionType.COUNT_INT),
            new QueryInfo("SELECT COUNT(*) as cnt FROM etr WHERE type IN ('a-etr', 'f-etr') AND deleted_at IS NULL AND ref_id IS NULL", "DB_ALL_ETR_AMORTISATION", "cnt", ExtractionType.COUNT_INT),

			// ETR face value queries
//			new QueryInfo("SELECT SUM(face_value) as total FROM etr WHERE deleted_at IS NULL AND ref_id IS NULL", "DB_GET_SUM_FACE_VALUE", "total", ExtractionType.DOUBLE_VALUE),
//			new QueryInfo("SELECT AVG(face_value) as avg_val FROM etr WHERE deleted_at IS NULL AND ref_id IS NULL", "DB_GET_ETR_AVG_FACE_VALUE", "avg_val", ExtractionType.DOUBLE_VALUE),
//			new QueryInfo("SELECT MAX(face_value) as max_val FROM etr WHERE deleted_at IS NULL AND ref_id IS NULL", "DB_GET_MAX_ETR_FACE_VALUE", "max_val", ExtractionType.DOUBLE_VALUE),
//			new QueryInfo("SELECT MIN(face_value) as min_val FROM etr WHERE deleted_at IS NULL AND ref_id IS NULL", "DB_GET_MIN_ETR_FACE_VALUE", "min_val", ExtractionType.DOUBLE_VALUE),

			// ETR result queries
			new QueryInfo("SELECT MIN(PTV_value) as min_ptv FROM etr_result", "DB_GET_MIN_PTV_VALUE", "min_ptv", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT MAX(PTV_value) as max_ptv FROM etr_result", "DB_GET_MAX_PTV_VALUE", "max_ptv", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT SUM(PTV_value) as sum_ptv FROM etr_result", "DB_GET_SUM_PTV_VALUE", "sum_ptv", ExtractionType.DOUBLE_VALUE),
			new QueryInfo("SELECT AVG(PTV_value) as avg_ptv FROM etr_result", "DB_GET_AVG_PTV_VALUE", "avg_ptv", ExtractionType.DOUBLE_VALUE)
		};
		
		// Execute all queries using a loop
		for (QueryInfo queryInfo : queries) {
			executeQuery(queryInfo);
		}
	}
}
            