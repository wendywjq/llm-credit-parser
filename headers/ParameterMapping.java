package headers;


import com.shuoen.varshow.shvar.beans.LoanAcctType;
import com.shuoen.varshow.shvar.beans.LoanInfo;
import com.shuoen.varshow.shvar.beans.ProfessionInfo;
import com.shuoen.varshow.shvar.vars.params.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParameterMapping {
//    private static final Logger logger_business_error = LogManager.getLogger(Consts.LOGGER_BUSINESS_ERROR);

    public static boolean isFitPayFreqType(String payFreqTypeStr, PayFreqType payFreqType) {
        switch (payFreqType) {
            case MTH:
                return "月".equals(payFreqTypeStr);
            case OCCA:
                return "不定期".equals(payFreqTypeStr);
            default:
                return false;
        }
    }

    public static boolean isFitMtgType(String mtgTypeStr, MtgType mtgType) {
        switch (mtgType) {
            case MORTGAGE:
                return "抵押".equals(mtgTypeStr);
            case CREDIT:
                return "信用/免担保".equals(mtgTypeStr);
            case PLEDGE:
                return "质押".equals(mtgTypeStr);
            case ENSURE:
                return "保证".equals(mtgTypeStr);
            case COMPOSE:
                return mtgTypeStr != null && "组合".equals(mtgTypeStr.substring(0,2));
            case OTHER:
                return "其他".equals(mtgTypeStr);
            default:
                return false;
        }
    }

    public static boolean isFitLoanBusinessType(String businessType, String businessTypeConfig) {
        if (Utils.isEmpty(businessType)) return false;
        switch (businessTypeConfig.toLowerCase()) {
            case "house":
                return "个人住房商业贷款".equals(businessType) || "个人住房公积金贷款".equals(businessType);
            case "consumer":
                return businessType.contains("个人消费");
            case "business":
                return "个人经营性贷款".equals(businessType) || "经营性农户贷款".equals(businessType);
            case "allhouse":
                return "个人住房商业贷款".equals(businessType) || "个人商用房（含商住两用）贷款".equals(businessType) || "个人住房公积金贷款".equals(businessType);
            default:
                return false;
        }
    }

    public static boolean isFitLoanBusinessType(String businessType, LoanBusinessType loanBusinessType) {
        switch (loanBusinessType) { //COMHL|FUNDHL|PERSHL|STUL|CSML|HL|CARL|NHL|AGRIL|PERBIZL|OTHL
            case ALL:
                return true;
            case CC:
                return "贷记卡".equals(businessType);
            case COMHL:
                return "个人住房商业贷款".equals(businessType);
            case FUNDHL:
                return "个人住房公积金贷款".equals(businessType);
            case PERSHL:
                return "个人商用房（含商住两用）贷款".equals(businessType);
//            case COMBHL:
//                return "个人商用房（含商住两用）贷款".equals(businessType);
            case STUL:
                return "商业助学贷款".equals(businessType);
            case PSTUL:
                return "个人助学贷款".equals(businessType);
            case CSML:
                return "其他个人消费贷款".equals(businessType);
            case HL:
                return "个人住房商业贷款".equals(businessType) || "个人住房公积金贷款".equals(businessType) || "个人商用房（含商住两用）贷款".equals(businessType);
            case CARL:
                return "个人汽车消费贷款".equals(businessType);
            case NHL:
                return !"个人住房商业贷款".equals(businessType) && !"个人住房公积金贷款".equals(businessType) && !"个人商用房（含商住两用）贷款".equals(businessType);
            case AGRIL:
                return "农户贷款".equals(businessType);
            case PERBIZL:
                return "个人经营性贷款".equals(businessType);
            case OTHL:
                return "其他贷款".equals(businessType);
            case STKT:
                return "约定购回式证券交易".equals(businessType) || "其他证券类融资".equals(businessType) || "融资融券业务".equals(businessType) || "股票质押式回购交易".equals(businessType);
            case AGSTKT:
                return "约定购回式证券交易".equals(businessType);
            case OHSTKL:
                return "其他证券类融资".equals(businessType);
            case MARSTKT:
                return "融资融券业务".equals(businessType);
            case PLESTKT:
                return "股票质押式回购交易".equals(businessType);
            default:
                return false;
        }
    }

    public static boolean isFitCurrencyType(String currencyTypeStr, CurrencyType currencyType) {
        switch (currencyType) {
            case RMB:
                return "人民币".equals(currencyTypeStr);
            case USD:
                return "美元".equals(currencyTypeStr);
            case JPY:
                return "日元".equals(currencyTypeStr);
            case GBP:
                return "英镑".equals(currencyTypeStr);
            case EUR:
                return "欧元".equals(currencyTypeStr);
            case ALL:
                return true;
            default:
                return false;
        }
    }

    public static boolean isFitSpecialTransType(String specialStr, SpecialTransType specialTransType)
    {
        switch (specialTransType){
            case PP:
                return specialStr != null && specialStr.contains("提前还款");
            case GC:
                return specialStr != null && specialStr.contains("担保人");
            case DP:
                return specialStr != null && specialStr.contains("展期");
            case PD:
                return "以资抵债".equals(specialStr);
            case OT:
                return (specialStr == null) || (!specialStr.contains("提前还款") && !specialStr.contains("担保人") && !specialStr.contains("展期") && !"以资抵债".equals(specialStr));
            default:
                return false;
        }
    }

    public static SpecialTransType getSpecialTransType(String specialStr) {
        switch (specialStr.toUpperCase()) {
            case "PP":
                return SpecialTransType.PP;
            case "GC":
                return SpecialTransType.GC;
            case "DP":
                return SpecialTransType.DP;
            case "OT":
                return SpecialTransType.OT;
            case "PD":
                return SpecialTransType.PD;
            default:
//                logger_business_error.warn("NO SpecialTransType: {}", specialStr);
                LogUtil.warn("NO SpecialTransType: {}", specialStr);
                return SpecialTransType.PP;
        }
    }

    public static boolean isFitFiveClassType(String fiveClassStr, FiveClassType fiveClassType) {
        switch (fiveClassType) {
            case NR:
                return "正常".equals(fiveClassStr);
            case FC:
                return "关注".equals(fiveClassStr);
            case SC:
                return "次级".equals(fiveClassStr);
            case DB:
                return "可疑".equals(fiveClassStr);
            case LS:
                return "损失".equals(fiveClassStr);
            default:
                return false;
        }
    }

    public static FiveClassType getFiveClassType(String fiveClassStr) {
        switch (fiveClassStr.toUpperCase()) {
            case "NR":
                return FiveClassType.NR;
            case "FC":
                return FiveClassType.FC;
            case "SC":
                return FiveClassType.SC;
            case "DB":
                return FiveClassType.DB;
            case "LS":
                return FiveClassType.LS;
            default:
//                logger_business_error.warn("NO FiveClassType: {}", fiveClassStr);
                LogUtil.warn("NO FiveClassType: {}", fiveClassStr);
                return FiveClassType.NR;
        }
    }

    public static boolean isFitAcctStatusType(String status, CalcAcctStatusType calcAcctStatusType) {
        switch (calcAcctStatusType) {
            case NR:
                return "正常".equals(status);
            case OD:
                return "逾期".equals(status);
            case BD:
                return "呆账".equals(status);
            case RO:
                return "转出".equals(status);
            case ST:
                return "结清".equals(status);
            case CA:
                return "销户".equals(status);
            case FE:
                return "冻结".equals(status);
            case SP:
                return "止付".equals(status);
            case NA:
                return "未激活".equals(status);
            case OP:
                return "正常".equals(status) || "逾期".equals(status) || "冻结".equals(status) || "止付".equals(status) || "转出".equals(status) || "呆账".equals(status);
            case ALL:
                return true;
            default:
                return false;
        }
    }

    public static boolean isFitAcctTypeLoan(LoanAcctType loanAcctType) {
        return LoanAcctType.NonrevolvingCredit.equals(loanAcctType) || LoanAcctType.SubAccountUnderRevolvingQuota.equals(loanAcctType)
                || LoanAcctType.RevolvingCredit.equals(loanAcctType);
    }

    //贷款|所有|信用卡|狭义贷款|非循环贷账户|循环额度下分账户|循环贷账户|贷记卡账户|准贷记卡账户
    public static boolean isFitAcctType(LoanAcctType loanAcctType, CalcAcctType calcAcctType)
    {
        switch (calcAcctType)
        {
            case All:
                return true;
            case Loan:
                return LoanAcctType.NonrevolvingCredit.equals(loanAcctType) || LoanAcctType.RevolvingCredit.equals(loanAcctType)
                        || LoanAcctType.SubAccountUnderRevolvingQuota.equals(loanAcctType);
            case CreditCard:
                return LoanAcctType.CreditCard.equals(loanAcctType);
            case NonrevolvingCredit:
                return LoanAcctType.NonrevolvingCredit.equals(loanAcctType);
            case RevolvingCredit:
                return LoanAcctType.RevolvingCredit.equals(loanAcctType);
            case SubAccountUnderRevolvingQuota:
                return LoanAcctType.SubAccountUnderRevolvingQuota.equals(loanAcctType);
            case QuasiCreditCard:
                return LoanAcctType.QuasiCreditCard.equals(loanAcctType);
            case NarrowLoan:
                return LoanAcctType.NonrevolvingCredit.equals(loanAcctType) || LoanAcctType.RevolvingCredit.equals(loanAcctType);
            case AllCreditCard:
                return LoanAcctType.CreditCard.equals(loanAcctType) || LoanAcctType.QuasiCreditCard.equals(loanAcctType);
            default:
                return false;
        }
    }

    public static PayFreqType getPayFreqType(String payFreqTypeAbbr) {
        switch (payFreqTypeAbbr.toUpperCase()) {
            case "MTH":
                return PayFreqType.MTH;
            case "OCCA":
                return PayFreqType.OCCA;
            default:
//                logger_business_error.error("NO PayFreqType: {}", payFreqTypeAbbr);
                LogUtil.warn("NO PayFreqType: {}", payFreqTypeAbbr);
                return PayFreqType.MTH;
        }
    }

    public static MtgType getMtgType(String mtgTypeAbbr) {
        switch (mtgTypeAbbr.toUpperCase()) {
            case "MORTGAGE":
                return MtgType.MORTGAGE;
            case "CREDIT":
                return MtgType.CREDIT;
            case "PLEDGE":
                return MtgType.PLEDGE;
            case "ENSURE":
                return MtgType.ENSURE;
            case "COMPOSE":
                return MtgType.COMPOSE;
            case "OTHER":
                return MtgType.OTHER;
            default:
//                logger_business_error.error("NO MtgType: {}", mtgTypeAbbr);
                LogUtil.warn("NO MtgType: {}", mtgTypeAbbr);
                return MtgType.MORTGAGE;
        }
    }

    public static boolean isValidZhuiChangBusinessType(String busiTypeStr)
    {
        return !Utils.isEmpty(busiTypeStr) ; //&& (busiTypeStr.contains("资产处置") || busiTypeStr.contains("代偿债务"));
    }

    public static LoanBusinessType getLoanBusinessType(String businessTypeAbbr) {
        switch (businessTypeAbbr.toUpperCase()) {
            case "ALL":
                return LoanBusinessType.ALL;
            case "COMHL":
                return LoanBusinessType.COMHL;
//            case "COMBHL":
//                return LoanBusinessType.COMBHL;
            case "FUNDHL":
                return LoanBusinessType.FUNDHL;
            case "PERSHL":
                return LoanBusinessType.PERSHL;
            case "STUL":
                return LoanBusinessType.STUL;
            case "PSTUL":
                return LoanBusinessType.PSTUL;
            case "CSML":
                return LoanBusinessType.CSML;
            case "HL":
                return LoanBusinessType.HL;
            case "CARL":
                return LoanBusinessType.CARL;
            case "NHL":
                return LoanBusinessType.NHL;
            case "AGRIL":
                return LoanBusinessType.AGRIL;
            case "PERBIZL":
                return LoanBusinessType.PERBIZL;
            case "OTHL":
                return LoanBusinessType.OTHL;
            default:
                LogUtil.warn("NO LoanBusinessType: {}", businessTypeAbbr);
//                logger_business_error.warn("NO LoanBusinessType: {}", businessTypeAbbr);
                return LoanBusinessType.COMHL;
        }
    }

    public static CalcAcctType getAcctType(String acctType)
    {
        switch (acctType.toUpperCase())
        {
            case "PL":
                return CalcAcctType.Loan;
            case "ALL":
                return CalcAcctType.All;
            case "ACC":
                return CalcAcctType.AllCreditCard;
            case "NL":
                return CalcAcctType.NarrowLoan;
            case "NCL":
                return CalcAcctType.NonrevolvingCredit;
            case "CDA":
                return CalcAcctType.SubAccountUnderRevolvingQuota;
            case "CL":
                return CalcAcctType.RevolvingCredit;
            case "CC":
                return CalcAcctType.CreditCard;
            case "QCC":
                return CalcAcctType.QuasiCreditCard;
            default:
//                logger_business_error.warn("NO acctType: {}", acctType);
                LogUtil.warn("NO acctType: {}", acctType);
                return CalcAcctType.All;
        }
    }

    public static AcctUsedType getAcctUsedType(String acctType)
    {
        switch (acctType.toUpperCase())
        {
            case "MAX":
                return AcctUsedType.MAX;
            case "REPAY":
                return AcctUsedType.Repay;
            default:
//                logger_business_error.warn("NO AcctUsedType: {}", acctType);
                LogUtil.warn("NO AcctUsedType: {}", acctType);
                return AcctUsedType.MAX;
        }
    }

    public static TermAmtType getTermAmtType(String termAmtType)
    {
        switch (termAmtType.toUpperCase())
        {
            case "TERM":
                return TermAmtType.Term;
            case "AMT":
                return TermAmtType.Amt;
            default:
//                logger_business_error.warn("NO termAmtType: {}", termAmtType);
                LogUtil.warn("NO termAmtType: {}", termAmtType);
                return TermAmtType.Term;
        }
    }

    public static StatType getStatType(String statType) {
        switch (statType.toUpperCase()) {
            case "MAX":
                return StatType.MAX;
            case "MIN":
                return StatType.MIN;
            case "AVG":
                return StatType.AVG;
            case "SUM":
                return StatType.SUM;
            default:
//                logger_business_error.warn("NO statType: {}", statType);
                LogUtil.warn("NO statType: {}", statType);
                return StatType.MAX;
        }
    }

    public static CurrencyType getCurrencyType(String currencyType) {
        switch (currencyType.toUpperCase()) {
            case "RMB":
                return CurrencyType.RMB;
            case "USD":
                return CurrencyType.USD;
            case "JPY":
                return CurrencyType.JPY;
            case "GBP":
                return CurrencyType.GBP;
            case "EUR":
                return CurrencyType.EUR;
            case "ALL":
                return CurrencyType.ALL;
            default:
//                logger_business_error.warn("NO CurrencyType: {}", currencyType);
                LogUtil.warn("NO CurrencyType: {}", currencyType);
                return CurrencyType.ALL;
        }
    }

    public static CalcAcctStatusType getAcctStatusType(String acctStatusType)
    {
        switch (acctStatusType.toUpperCase())
        {
            case "NR":
                return CalcAcctStatusType.NR;
            case "OD":
                return CalcAcctStatusType.OD;
            case "BD":
                return CalcAcctStatusType.BD;
            case "RO":
                return CalcAcctStatusType.RO;
            case "ST":
                return CalcAcctStatusType.ST;
            case "CA":
                return CalcAcctStatusType.CA;
            case "FE":
                return CalcAcctStatusType.FE;
            case "SP":
                return CalcAcctStatusType.SP;
            case "NA":
                return CalcAcctStatusType.NA;
            case "OP":
                return CalcAcctStatusType.OP;
            case "ALL":
                return CalcAcctStatusType.ALL;
            default:
//                logger_business_error.warn("NO acctStatusType: {}", acctStatusType);
                LogUtil.warn("NO acctStatusType: {}", acctStatusType);
                return CalcAcctStatusType.ALL;
        }
    }

    public static boolean isFitQueryReason(String queryReasonType,String queryReason){
        if (queryReasonType == null){
            return false;
        }
        switch (queryReasonType){
            case "PAP": return "贷款审批".equalsIgnoreCase(queryReason) || "信用卡审批".equalsIgnoreCase(queryReason) || "本人查询".equalsIgnoreCase(queryReason);
            case "PA": return "贷款审批".equalsIgnoreCase(queryReason) || "信用卡审批".equalsIgnoreCase(queryReason);
            default:return  false;
        }

    }
    public static String getHFPayStatueCode(String desc) {
        if(desc == null) {
            return null;
        }
        switch (desc) {
            case "缴存":
                return "1";
            case "封存":
                return "2";
            case "销户":
                return "3";
            default:
                return null;
        }
    }

    public static boolean isFitQueryReason(QueryReasonType queryReasonType,String queryReason){
        if (Utils.isEmpty(queryReason)){
            return false;
        }
        switch (queryReasonType){
            case PLQ:
                return "贷款审批".equalsIgnoreCase(queryReason);
            case ACCQ:
                return "信用卡审批".equalsIgnoreCase(queryReason);
            case CCPLQ:
                return "贷款审批".equalsIgnoreCase(queryReason) || "信用卡审批".equalsIgnoreCase(queryReason);
            case PPQ:
                return "保前审查".equalsIgnoreCase(queryReason);
            case FINQ:
                return "融资审批".equalsIgnoreCase(queryReason);
            case PERSQ:
                return "本人查询".equalsIgnoreCase(queryReason);
            case ALQ:
                return "贷后管理".equalsIgnoreCase(queryReason);
            case GQEQ:
                return "担保资格审查".equalsIgnoreCase(queryReason);
            case SECQ:
                return "特约商户实名审查".equalsIgnoreCase(queryReason);
            case ALL:
                return true;
            default: return false;
        }
    }

    public static String getProfessionField(ProfessionInfo professionInfo, String param) {
        switch (param){
            case "LEMPTYPE":return professionInfo.getEMP_Type();
            case "LOCCUP":return professionInfo.getOccupation();
            case "LINDUS":return professionInfo.getIndustry();
            case "LPOSI":return professionInfo.getPosition();
            case "LPROFTLE":return professionInfo.getProfessional_Title();
            default:
                LogUtil.warn("NO Profession Type: {}", param);
                return null;
        }
    }

    public static Integer getEmpTypeValue(String empType) {
        if (empType == null)
            return null;
        switch (empType){
            case "机关、事业单位": return 9;
            case "国有企业": return 6;
            case "外资企业": return 4;
            case "个体、私营企业": return 3;
            case "其他（包括三资企业、民营企业、民间团体等）": return 2;
            case "未知": return 1;
            default:
                LogUtil.warn("NO EmpType: {}", empType);
                return null;
        }
    }

    public static Integer getPositionValue(String position) {
        if (position == null)
            return null;
        switch (position){
            case "高级领导": return 9;
            case "中级领导": return 6;
            case "一般员工": return 4;
            case "其他": return 3;
            case "未知": return 2;
//            case "未知": return 1;
            default:
                LogUtil.warn("NO position: {}", position);
                return null;
        }
    }

    public static Integer getTitleValue(String title) {
        if (title == null)
            return null;
        switch (title){
            case "高级": return 9;
            case "中级": return 6;
            case "无": return 2;
            case "初级": return 4;
            case "未知": return 3;
//            case "未知": return 1;
            default:
                LogUtil.warn("NO title: {}", title);
                return null;
        }
    }

    public static QueryReasonType getQueryReason(String type){
        switch (type){
            case "PLQ":
                return QueryReasonType.PLQ;
            case "ACCQ":
                return QueryReasonType.ACCQ;
            case "CCPLQ":
                return QueryReasonType.CCPLQ;
            case "PPQ":
                return QueryReasonType.PPQ;
            case "FINQ":
                return QueryReasonType.FINQ;
            case "PERSQ":
                return QueryReasonType.PERSQ;
            case "ALQ":
                return QueryReasonType.ALQ;
            case "GQEQ":
                return QueryReasonType.GQEQ;
            case "SECQ":
                return QueryReasonType.SECQ;
            case "ALL":
            case "ALLQ":
                return QueryReasonType.ALL;
            default:
                LogUtil.warn("NO QueryReasonType: {}", type);
                return QueryReasonType.PLQ;
        }
    }


    public static Boolean isOverDueFromRepayStatus(String repayStatus) {
        List<String> overDueStatusList = new ArrayList<>(Arrays.asList("1","2","3","4","5","6","7","B"));
        if(repayStatus != null && overDueStatusList.contains(repayStatus)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


    public static Double getAmt(LoanInfo x) {
        if(x.getAcct_Type() == null) {
            return null;
        }

        switch (x.getAcct_Type()) {
            case RevolvingCredit:return x.getCredit_Limit().doubleValue();
            case NonrevolvingCredit:return x.getAmt().doubleValue();
            case SubAccountUnderRevolvingQuota:return x.getAmt().doubleValue();
            case CreditCard:
            case QuasiCreditCard:return x.getCredit_Limit().doubleValue();
            default:return null;
        }
    }

    public static Boolean isAnotherOrg(String orgCode) {
        if(orgCode != null && orgCode.length() == 2 && orgCode.equals(orgCode.toUpperCase())){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;

    }

    public static boolean fitConUpdateStatus(LoanAcctType acctType, String status) {
        boolean value = false;
        if (ParameterMapping.isFitAcctType(acctType, CalcAcctType.Loan)) {
            if (ParameterMapping.isFitAcctStatusType(status, CalcAcctStatusType.NR)
                    || ParameterMapping.isFitAcctStatusType(status, CalcAcctStatusType.OD)
                    || ParameterMapping.isFitAcctStatusType(status, CalcAcctStatusType.BKSP)
                    || ParameterMapping.isFitAcctStatusType(status, CalcAcctStatusType.IC)
                    || ParameterMapping.isFitAcctStatusType(status, CalcAcctStatusType.CL)
                    || ParameterMapping.isFitAcctStatusType(status, CalcAcctStatusType.JR)) {
                value = true;
            }
        } else if (ParameterMapping.isFitAcctType(acctType, CalcAcctType.AllCreditCard)) {
            if (ParameterMapping.isFitAcctStatusType(status, CalcAcctStatusType.NR)
                    || ParameterMapping.isFitAcctStatusType(status, CalcAcctStatusType.FE)
                    || ParameterMapping.isFitAcctStatusType(status, CalcAcctStatusType.SP)
                    || ParameterMapping.isFitAcctStatusType(status, CalcAcctStatusType.BKSP)
                    || ParameterMapping.isFitAcctStatusType(status, CalcAcctStatusType.JCRV)) {
                value =  true;
            }
        }
        return value;
    }
}
