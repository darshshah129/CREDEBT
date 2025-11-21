package Compare;

import org.testng.annotations.Test;
import utils.Common;
import utils.Locators;
import Web.Smoke.*;
import DB.DatabaseSmokeTest.*;

import static utils.Common.compareDB_FE;

public class Compare {
    @Test
    public void Compare() throws Exception {
        compareDB_FE("DB_GET_A_ETR_RECORDS","FE_GET_A_ETR_RECORDS");
        compareDB_FE("DB_GET_B_ETR_RECORDS","FE_GET_B_ETR_RECORDS");
        compareDB_FE("DB_GET_C_ETR_RECORDS","FE_GET_C_ETR_RECORDS");
        compareDB_FE("DB_GET_D_ETR_RECORDS","FE_GET_D_ETR_RECORDS");
        compareDB_FE("DB_GET_F_ETR_RECORDS","FE_GET_F_ETR_RECORDS");
        compareDB_FE("DB_GET_CREDIT_NOTE_RECORDS","FE_GET_CREDIT_NOTE_RECORDS");
        compareDB_FE("DB_GET_DEDUCTION_RECORDS","FE_GET_DEDUCTION_RECORDS");
        compareDB_FE("DB_GET_DEPOSIT_RECORDS","FE_GET_DEPOSIT_RECORDS");
        compareDB_FE("DB_GET_ICP_RECORDS","FE_GET_ICP_RECORDS");
    }
}
