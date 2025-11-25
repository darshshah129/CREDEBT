package Compare;

import org.testng.annotations.Test;

import utils.Common;


public class Compare {
    Common common = new Common();
   
    @Test
    public void compare() throws Exception {
       common.compareDB_FE("DB_GET_A_ETR_RECORDS","FE_GET_A_ETR_RECORDS");
       common.compareDB_FE("DB_GET_B_ETR_RECORDS","FE_GET_B_ETR_RECORDS");
       common.compareDB_FE("DB_GET_C_ETR_RECORDS","FE_GET_C_ETR_RECORDS");
       common.compareDB_FE("DB_GET_D_ETR_RECORDS","FE_GET_D_ETR_RECORDS");
       common.compareDB_FE("DB_GET_F_ETR_RECORDS","FE_GET_F_ETR_RECORDS");
       common.compareDB_FE("DB_GET_CREDIT_NOTE_RECORDS","FE_GET_CREDIT_NOTE_RECORDS");
       common.compareDB_FE("DB_GET_DEDUCTION_RECORDS","FE_GET_DEDUCTION_RECORDS");
       common.compareDB_FE("DB_GET_DEPOSIT_RECORDS","FE_GET_DEPOSIT_RECORDS");
       common.compareDB_FE("DB_GET_ICP_RECORDS","FE_GET_ICP_RECORDS");
       common.compareDB_FE("DB_GET_OCPA_RECORDS","FE_GET_OCPA_RECORDS");
       common.compareDB_FE("DB_GET_IDP_RECORDS","FE_GET_IDP_RECORDS");
       common.compareDB_FE("DB_GET_ORP_RECORDS","FE_GET_ORP_RECORDS");
       common.compareDB_FE("DB_GET_ORR_RECORDS","FE_GET_ORR_RECORDS");
       common.compareDB_FE("DB_GET_RSA_RECORDS","FE_GET_RSA_RECORDS");
       common.compareDB_FE("DB_GET_CREDIT_NOTE_RECORDS","FE_GET_CREDIT_NOTE_RECORDS");
       common.compareDB_FE("DB_GET_OPAC_RECORDS","FE_GET_OPAC_RECORDS");
       common.compareDB_FE("DB_GET_ALL_ETR_LIKE_RECORDS","FE_GET_ALL_ETR_LIKE_RECORDS");
       common.compareDB_FE("DB_GET_ASSET_TYPE_RECORDS","FE_GET_ASSET_TYPE_RECORDS");
       common.compareDB_FE("DB_GET_TRADE_TYPE_RECORDS","FE_GET_TRADE_TYPE_RECORDS");
       common.compareDB_FE("DB_GET_DEBITS_TYPE_RECORDS","FE_GET_DEBITS_TYPE_RECORDS");
       common.compareDB_FE("DB_GET_CREDITS_TYPE_RECORDS","FE_GET_CREDITS_TYPE_RECORDS");
    }
}
