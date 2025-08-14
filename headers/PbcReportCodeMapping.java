package headers;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.shuoen.varshow.shvar.beans.LoanAcctType;

;

/**
 * @author shan.wei@suanhua.com
 * @date 2019/4/11 15:46
 */
public class PbcReportCodeMapping {

    /**
     * 根据个人证件类型代码表获取 code 对应的 个人证件类型
     * @param code 个人证件类型代码
     * @return 个人证件类型
     */
    public static String getIdTypeStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "1":
                return "户口簿";
            case "2":
                return "护照";
            case "5":
                return "港澳居民来往内地通行证";
            case "6":
                return "台湾同胞来往内地通行证";
            case "8":
                return "外国人居留证";
            case "9":
                return "警官证";
            case "A":
                return "香港身份证";
            case "B":
                return "澳门身份证";
            case "C":
                return "台湾身份证";
            case "X":
                return "其他证件";
            case "10":
                return "身份证";
            case "20":
                return "军人身份证件";
            default:
                return code;
        }
    }


    /**
     * 根据个人证件类型代码表获取 code 对应的 个人证件类型
     * @param code 个人证件类型代码
     * @return 个人证件类型
     */
    public static String getIdTypeCode(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "户口簿":
                return "1";
            case "护照":
                return "2";
            case "港澳居民来往内地通行证":
                return "5";
            case "台湾同胞来往内地通行证":
                return "6";
            case "外国人居留证":
                return "8";
            case "警官证":
                return "9";
            case "香港身份证":
                return "A";
            case "澳门身份证":
                return "B";
            case "台湾身份证":
                return "C";
            case "其他证件":
                return "X";
            case "身份证":
                return "10";
            case "军人身份证件":
                return "20";
            default:
                return code;
        }
    }


    /**
     * 根据查询原因代码表获取 code 对应的 查询原因
     * @param code 查询原因代码
     * @return 查询原因
     */
    public static String getQueryReasonStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "01":
                return "贷后管理";
            case "02":
                return "贷款审批";
            case "03":
                return "信用卡审批";
            case "08":
                return "担保资格审查";
            case "09":
                return "司法调查";
            case "16":
                return "公积金提取复核查询";
            case "18":
                return "股指期货开户";
            case "19":
                return "特约商户实名审查";
            case "20":
                return "保前审查";
            case "21":
                return "保后管理";
            case "22":
                return "法人代理、负责人、高管等资信审查";
            case "23":
                return "客户准入资格审查";
            case "24":
                return "融资审批";
            case "25":
                return "资信审查";
            case "26":
                return "额度审批";
            default:
                return code;
        }
    }

    /**
     *  根据性别代码表获取 code 对应的 性别
     * @param code 性别代码
     * @return 性别
     */
    public static String getGenderStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "0":
                return "未知";
            case "1":
                return "男";
            case "2":
                return "女";
            case "9":
                return "未说明";
            default:
                return code;
        }
    }

    /**
     *  根据学历代码表获取 code 对应的 学历
     * @param code 学历代码
     * @return 学历
     */
    public static String getEduLevelStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "10":
                return "研究生";
            case "20":
                return "本科";
            case "30":
                return "大专";
            case "40":
                return "中专、职高、技校";
            case "60":
                return "高中";
            case "90":
                return "其他";
            case "91":
                return "初中及以下";
            case "99":
                return "未知";
            default:
                return code;
        }
    }

    /**
     *  根据学位代码表获取 code 对应的 学位
     * @param code 学位代码
     * @return 学位
     */
    public static String getEduDegreeStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "0":
                return "其他";
            case "1":
                return "名誉博士";
            case "2":
                return "博士";
            case "3":
                return "硕士";
            case "4":
                return "学士";
            case "5":
                return "无";
            case "9":
                return "未知";
            default:
                return code;
        }
    }

    /**
     *  根据从业状况代码表获取 code 对应的 就业状况
     * @param code 从业状况代码
     * @return 就业状况
     */
    public static String getWorkStatusStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "11":
                return "国家公务员";
            case "13":
                return "专业技术人员";
            case "17":
                return "职员";
            case "21":
                return "企业管理人员";
            case "24":
                return "工人";
            case "27":
                return "农民";
            case "31":
                return "学生";
            case "37":
                return "现役军人";
            case "51":
                return "自由职业者";
            case "54":
                return "个体经营者";
            case "70":
                return "无业人员";
            case "80":
                return "退（离）休人员";
            case "90":
                return "其他";
            case "91":
                return "在职";
            case "99":
                return "未知";
            default:
                return code;
        }
    }

    /**
     *  根据婚姻状况代码表获取 code 对应的 婚姻状况
     * @param code 婚姻状况代码
     * @return 婚姻状况
     */
    public static String getMaritalStatusStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "10":
                return "未婚";
            case "20":
                return "已婚";
            case "30":
                return "丧偶";
            case "40":
                return "离婚";
            case "91":
                return "单身";
            case "99":
                return "未知";
            default:
                return code;
        }
    }

    /**
     *  根据居住状况代码表获取 code 对应的 居住状况
     * @param code 居住状况代码
     * @return 居住状况
     */
    public static String getResidenceConditionStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "1":
                return "自置";
            case "2":
                return "按揭";
            case "3":
                return "亲属楼宇";
            case "4":
                return "集体宿舍";
            case "5":
                return "租房";
            case "6":
                return "共有住宅";
            case "7":
                return "其他";
            case "11":
                return "自有";
            case "12":
                return "借住";
            case "9":
                return "未知";
            default:
                return code;
        }
    }

    /**
     *  根据单位性质代码表获取 code 对应的 单位性质
     * @param code 单位性质代码
     * @return 单位性质
     */
    public static String getEmpTypeStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "10":
                return "机关、事业单位";
            case "20":
                return "国有企业";
            case "30":
                return "外资企业";
            case "40":
                return "个体、私营企业";
            case "50":
                return "其他（包括三资企业、民营企业、民间团体等）";
            case "99":
                return "未知";
            default:
                return code;
        }
    }

    /**
     *  根据职业代码表获取 code 对应的 职业
     * @param code 职业代码
     * @return 职业
     */
    public static String getOccupationStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "0":
                return "国家机关、党群组织、企业、事业单位负责人";
            case "1":
                return "专业技术人员";
            case "3":
                return "办事人员和有关人员";
            case "4":
                return "商业、服务业人员";
            case "5":
                return "农、林、牧、渔、水利业生产人员";
            case "6":
                return "生产、运输设备操作人员及有关人员";
            case "X":
                return "军人";
            case "Y":
                return "不便分类的其他从业人员";
            case "Z":
                return "未知";
            default:
                return code;
        }
    }

    /**
     *  根据行业代码表获取 code 对应的 行业
     * @param code 行业代码
     * @return 行业
     */
    public static String getIndustryStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "A":
                return "农、林、牧、渔业";
            case "B":
                return "采矿业";
            case "C":
                return "制造业";
            case "D":
                return "电力、热力、燃气及水生产和供应业";
            case "E":
                return "建筑业";
            case "F":
                return "批发和零售业";
            case "G":
                return "交通运输、仓储和邮储业";
            case "H":
                return "住宿和餐饮业";
            case "I":
                return "信息传输、软件和信息技术服务业";
            case "J":
                return "金融业";
            case "K":
                return "房地产业";
            case "L":
                return "租赁和商务服务业";
            case "M":
                return "科学研究和技术服务业";
            case "N":
                return "水利、环境和公共设施管理业";
            case "O":
                return "居民服务、修理和其他服务业";
            case "P":
                return "教育";
            case "Q":
                return "卫生和社会工作";
            case "R":
                return "文化、体育和娱乐业";
            case "S":
                return "公共管理、社会保障和社会组织";
            case "T":
                return "国际组织";
            case "9":
                return "未知";
            default:
                return code;
        }
    }

    /**
     *  根据职务代码表获取 code 对应的 职务
     * @param code 职务代码
     * @return 职务
     */
    public static String getPositionStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "1":
                return "高级领导";
            case "2":
                return "中级领导";
            case "3":
                return "一般员工";
            case "4":
                return "其他";
            case "9":
                return "未知";
            default:
                return code;
        }
    }

    /**
     *  根据职称代码表获取 code 对应的 职称
     * @param code 职称代码
     * @return 职称
     */
    public static String getProfessionalTitleStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "0":
                return "无";
            case "1":
                return "高级";
            case "2":
                return "中级";
            case "3":
                return "初级";
            case "9":
                return "未知";
            default:
                return code;
        }
    }

    /**
     *  拼接 code 对应的 个人信用报告数字解读 相对位置 字段
     * @param code 相对位置数字
     * @return 相对位置
     */
    public static String getPercentileRankStr(Integer code) {
        if(code == null) {
            return null;
        }

        if(code == -1) {
            return "-1";
        }

        return ">" + String.valueOf(code) + "%";
    }

    /**
     * 根据分数说明代码表获取 code 对应的 分数说明
     * @param code 分数说明代码
     * @return 分数说明
     */
    public static String getScoreDesStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "00":
                return "无影响因素";
            case "01":
                return "存在逾期还款记录";
            case "02":
                return "存在展期记录";
            case "03":
                return "当前债务相对较多";
            case "04":
                return "当前信用卡债务笔数相对较多";
            case "05":
                return "当前债务笔数相对较多";
            case "06":
                return "当前信用卡额度使用率相对较高";
            case "07":
                return "信用历史较短";
            case "08":
                return "信用卡历史较短";
            case "09":
                return "近期新增债务笔数较多";
            case "10":
                return "近期硬查询次数较多";
            case "11":
                return "近期没有信用活动";
            case "12":
                return "信用活动相对较为单一";
            case "13":
                return "近期信用卡息相对不足";
            case "14":
                return "近期贷款信息相对不足";
            case "15":
                return "近期信用息相对不足";
            case "16":
                return "债务种类相对不够丰富";
            case "91":
                return "无信贷记录";
            case "92":
                return "信用历史小于 3个月";
            case "93":
                return "缺少近两年的信贷息";
            case "99":
                return "其他";
            default:
                return code;
        }
    }

    /**
     * todo 这个代码表不全
     * 根据后付费业务类型代码表获取 code 对应的 后付费业务类型
     * @param code 后付费业务类型代码
     * @return 后付费业务类型
     */
    public static String getPostpaidBusiTypeStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "1":
                return "电信业务";
            case "2":
                return "自来水业务";
            default:
                return null;
        }
    }

    /**
     * 根据公共信息类型代码表获取 code 对应的 公共信息类型
     * @param code 公共信息类型代码
     * @return 公共信息类型
     */
    public static String getPublicInfoTypeStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "1":
                return "欠税信息";
            case "2":
                return "民事判决信息";
            case "3":
                return "强制执行信息";
            case "4":
                return "行政处罚信息";
            default:
                return code;
        }
    }

    /**
     * 根据机构类型代码表获取 code 对应的 机构类型
     * @param code 机构类型代码
     * @return 机构类型
     */
    public static String getOrgTypeStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "11":
                return "商业银行";
            case "12":
                return "村镇银行";
            case "14":
                return "住房储蓄银行";
            case "15":
                return "外资银行";
            case "16":
                return "财务公司";
            case "21":
                return "信托公司";
            case "22":
                return "融资租赁公司";
            case "23":
                return "汽车金融公司";
            case "24":
                return "消费金融公司";
            case "25":
                return "贷款公司";
            case "26":
                return "金融资产管理公司";
            case "31":
                return "证券公司";
            case "41":
                return "保险公司";
            case "51":
                return "小额贷款公司";
            case "52":
                return "公积金管理中心";
            case "53":
                return "融资担保公司";
            case "54":
                return "保理公司";
            case "99":
                return "其他机构";
            default:
                return code;
        }
    }

    /**
     * todo 待确认逻辑
     * 根据机构类型和机构编码返回机构名称
     * @param orgType 机构类型
     * @param orgCode 机构编码
     * @return 机构名称
     */
    public static String getOrgNameStr(String orgType, String orgCode) {
        if(orgCode == null) {
            return null;
        }

        String reg="^[A-Z]{2}$";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(orgCode);

        // 若不是2位大写字母，直接返回机构码作为机构名称
        if(!matcher.matches()) {
            return orgCode;
        }

        String rstStr;
        if(orgType == null) {
            rstStr = "“" + orgCode + "”";
        } else {
            rstStr = orgType + "“" + orgCode + "”";
        }
        return rstStr;
    }

    /**
     * 根据账户类型代码表获取 code 对应的 账户类型enum字段
     * @param code 账户类型代码
     * @return 账户类型enum字段
     */
    public static LoanAcctType getLoanAcctType(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "D1":
                return LoanAcctType.NonrevolvingCredit;
            case "R1":
                return LoanAcctType.RevolvingCredit;
            case "R2":
                return LoanAcctType.CreditCard;
            case "R3":
                return LoanAcctType.QuasiCreditCard;
            case "R4":
                return LoanAcctType.SubAccountUnderRevolvingQuota;
//            case "C1":
//                return "催收账户";
            default:
                return null;
        }
    }

    /**
     * 根据个人借贷交易业务种类代码表获取 code 对应的 个人借贷交易业务种类
     * @param code 个人借贷交易业务种类代码
     * @return 个人借贷交易业务种类
     */
    public static String getBusiTypeStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "11":
                return "个人住房商业贷款";
            case "12":
                return "个人商用房（含商住两用）贷款";
            case "13":
                return "个人住房公积金贷款";
            case "21":
                return "个人汽车消费贷款";
            case "31":
                return "个人助学贷款";
            case "32":
                return "国家助学贷款";
            case "33":
                return "商业助学贷款";
            case "41":
                return "个人经营性贷款";
            case "42":
                return "个人创业担保贷款";
            case "51":
                return "农户贷款";
            case "52":
                return "经营性农户贷款";
            case "53":
                return "消费性农户贷款";
            case "91":
                return "其他个人消费贷款";
            case "99":
                return "其他贷款";
            case "71":
                return "准贷记卡";
            case "81":
                return "贷记卡";
            case "82":
                return "大额专项分期卡";
            case "61":
                return "约定购回式证券交易";
            case "62":
                return "股票质押式回购交易";
            case "63":
                return "融资融券业务";
            case "64":
                return "其他证券类融资";
            case "92":
                return "融资租赁业务";
            case "A1":
                return "资产处置";
            case "B1":
                return "代偿债务";
            default:
                return code;
        }
    }

    /**
     * 根据组织机构业务种类代码表获取 code 对应的 组织机构业务种类
     * @param code 组织机构业务种类代码
     * @return 组织机构业务种类
     */
    public static String getCompanyBusiTypeStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "10":
                return "企业债";
            case "11":
                return "贷款";
            case "12":
                return "贸易融资";
            case "13":
                return "保理融资";
            case "14":
                return "融资租赁";
            case "15":
                return "证券类融资";
            case "16":
                return "透支";
            case "21":
                return "票据贴现";
            case "31":
                return "黄金借贷";
            case "41":
                return "垫款";
            case "51":
                return "资产处置";
            default:
                return code;
        }
    }

    /**
     * 根据担保方式代码表获取 code 对应的 担保方式
     * @param code 担保方式代码
     * @return 担保方式
     */
    public static String getGuaranteeTypeStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "1":
                return "质押";
            case "2":
                return "抵押";
            case "3":
                return "保证";
            case "4":
                return "信用/免担保";
            case "5":
                return "组合（含保证）";
            case "6":
                return "组合（不含保证）";
            case "7":
                return "农户联保";
            case "9":
                return "其他";
            default:
                return code;
        }
    }

    /**
     * 根据还款频率代码表获取 code 对应的 还款频率
     * @param code 还款频率代码
     * @return 还款频率
     */
    public static String getPayFreqStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "01":
                return "日";
            case "02":
                return "周";
            case "03":
                return "月";
            case "04":
                return "季";
            case "05":
                return "半年";
            case "06":
                return "年";
            case "07":
                return "一次性";
            case "08":
                return "不定期";
            case "12":
                return "旬";
            case "13":
                return "双周";
            case "14":
                return "双月";
            case "99":
                return "其他";

            default:
                return code;
        }
    }

    /**
     * 根据还款方式代码表获取 code 对应的 还款方式
     * @param code 还款方式代码
     * @return 还款方式
     */
    public static String getPayTypeStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "11":
                return "分期等额本息";
            case "12":
                return "分期等额本金";
            case "13":
                return "到期还本分结息";
            case "14":
                return "等比累进分期还款";
            case "15":
                return "等额累进分期还款";
            case "19":
                return "其他类型分期还款";
            case "21":
                return "到期一次还本付息";
            case "22":
                return "预先付息到期还本";
            case "23":
                return "随时还";
            case "29":
                return "其他";
            case "31":
                return "按期结息，到期还本";
            case "32":
                return "按期结息，自由还本";
            case "33":
                return "按期计算还本付息";
            case "39":
                return "循环贷款下其他还方式";
            case "90":
                return "不区分还款方式";
            default:
                return code;
        }
    }

    /**
     * 根据共同借款标志代码表获取 code 对应的 共同借款标志
     * @param code 共同借款标志代码
     * @return 共同借款标志
     */
    public static String getCommonLoanMarkStr(String code) {
        if (code == null) {
            return null;
        }

        switch (code) {
            case "0":
                return "无";
            case "1":
                return "主借款人";
            case "2":
                return "从借款人";
            default:
                return code;
        }
    }

    /**
     * 根据账户类型由不同的账户状态代码表获取 code 对应的 账户状态
     * @param acctTypeCode 账户类型代码
     * @param code 账户状态代码
     * @return 账户状态
     */
    public static String getStatusStr(String acctTypeCode, String code) {
        if(acctTypeCode == null || code == null) {
            return null;
        }

        switch (acctTypeCode) {
            case "D1":
                return getStatusStrForD1(code);
            case "R1":
                return getStatusStrForR1(code);
            case "R2":
            case "R3":
                return getStatusStrForR2R3(code);
            case "R4":
                return getStatusStrForR4(code);
            case "C1":
                return getStatusStrForC1(code);
            default:
                return null;
        }
    }

    /**
     * 根据 D1 账户状态代码表获取 code 对应的 账户状态
     * @param code D1 账户状态代码
     * @return 账户状态
     */
    private static String getStatusStrForD1(String code) {
        switch (code) {
            case "1":
                return "正常";
            case "2":
                return "逾期";
            case "3":
                return "结清";
            case "4":
                return "呆账";
            case "5":
                return "转出";
            case "6":
                return "担保物不足";
            case "7":
                return "强制平仓";
            case "8":
                return "司法追偿";
            default:
                return code;
        }
    }

    /**
     * 根据 R1 账户状态代码表获取 code 对应的 账户状态
     * @param code R1 账户状态代码
     * @return  账户状态
     */
    private static String getStatusStrForR1(String code) {
        switch (code) {
            case "1":
                return "正常";
            case "2":
                return "逾期";
            case "3":
                return "结清";
            case "4":
                return "呆账";
            case "5":
                return "银行止付";
            case "6":
                return "担保物不足";
            case "8":
                return "司法追偿";
            default:
                return code;
        }
    }

    /**
     * 根据 R2/R3 账户状态代码表获取 code 对应的 账户状态
     * @param code R2/R3 账户状态代码
     * @return 账户状态
     */
    private static String getStatusStrForR2R3(String code) {
        switch (code) {
            case "1":
                return "正常";
            case "2":
                return "冻结";
            case "3":
                return "止付";
            case "31":
                return "银行止付";
            case "4":
                return "销户";
            case "5":
                return "呆账";
            case "6":
                return "未激活";
            case "8":
                return "司法追偿";
            default:
                return code;
        }
    }

    /**
     * 根据 R4 账户状态代码表获取 code 对应的 账户状态
     * @param code R4 账户状态代码
     * @return 账户状态
     */
    private static String getStatusStrForR4(String code) {
        switch (code) {
            case "1":
                return "正常";
            case "2":
                return "逾期";
            case "3":
                return "结清";
            case "4":
                return "呆账";
            case "6":
                return "担保物不足";
            case "8":
                return "司法追偿";
            default:
                return code;
        }
    }

    /**
     * 根据 C1 账户状态代码表获取 code 对应的 账户状态
     * @param code C1 账户状态代码
     * @return 账户状态
     */
    private static String getStatusStrForC1(String code) {
        switch (code) {
            case "1":
                return "催收";
            case "2":
                return "结束";
            default:
                return code;
        }
    }

    /**
     * 根据五级分类代码表获取 code 对应的 五级分类
     * @param code 五级分类代码
     * @return 五级分类
     */
    public static String getFiveClassStatus(String code) {
        if (code == null) {
            return null;
        }

        switch (code) {
            case "正常":
                return "1";
            case "关注":
                return "2";
            case "次级":
                return "3";
            case "可疑":
                return "4";
            case "损失":
                return "5";
            case "未分类":
                return "9";
            default:
                return null;
        }
    }

    /**
     * 根据五级分类代码表获取 code 对应的 五级分类
     * @param code 五级分类代码
     * @return 五级分类
     */
    public static String getFiveClassStatusStr(String code) {
        if (code == null) {
            return null;
        }

        switch (code) {
            case "1":
                return "正常";
            case "2":
                return "关注";
            case "3":
                return "次级";
            case "4":
                return "可疑";
            case "5":
                return "损失";
            case "9":
                return "未分类";
            default:
                return code;
        }
    }

    /**
     * 根据特殊交易类型代码表获取 code  对应的 特殊交易类型
     * @param code 特殊交易类型代码
     * @return 特殊交易类型
     */
    public static String getSpecialTradeTypeStr(String code) {
        if (code == null) {
            return null;
        }

        switch (code) {
            case "1":
                return "展期";
            case "2":
                return "担保人（第三方）代偿";
            case "3":
                return "以资抵债";
            case "4":
                return "提前还款（包括提前归还部分本金、还款期限不变，以及缩短还款期限两种情况）";
            case "5":
                return "提前结清";
            case "6":
                return "强制平仓，未结清";
            case "7":
                return "强制平仓，已结清";
            case "8":
                return "司法追偿";
            case "9":
                return "其他";
            case "11":
                return "债务减免";
            case "12":
                return "资产剥离";
            case "13":
                return "资产转让";
            case "14":
                return "信用卡个性化分期";
            case "16":
                return "银行主动延期";
            case "17":
                return "强制平仓";
            default:
                return code;
        }
    }

    /**
     * 根据授信额度用途代码表获取 code 对应的 授信额度用途
     * @param code 授信额度用途代码
     * @return 授信额度用途
     */
    public static String getCpmarkCreditUseStr(String code) {
        if (code == null) {
            return null;
        }

        switch (code) {
            case "10":
                return "循环贷款额度";
            case "20":
                return "非循环贷款额度";
            case "30":
                return "信用卡共享额度";
            case "31":
                return "信用卡独立额度";
            default:
                return code;
        }
    }

    /**
     * todo 未完成
     * 根据国标币种代码表获取 code 对应的 币种
     * @param code 国标币种代码
     * @return 币种
     */
    public static String getCurrencyTypeStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "CNY":
                return "人民币";
            case "USD":
                return "美元";
            case "JPY":
                return "日元";
            case "GBP":
                return "英镑";
            case "EUR":
                return "欧元";
            default:
                return code;
        }
    }

    /**
     * 根据相关还款责任人类型代码表获取 code 对应的 责任人类型
     * @param code 相关还款责任人类型代码
     * @return 责任人类型
     */
    public static String getResponsePersonTyepStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "1":
                return "共同借款人";
            case "2":
                return "保证人";
            case "3":
                return "票据承兑人";
            case "4":
                return "应收账款债务人";
            case "5":
                return "供应链中核心企业";
            case "9":
                return "其他";
            default:
                return code;
        }
    }

    /**
     * 根据time拼接成最新还款记录标题
     * @param time 时间
     * @return 最新还款记录标题
     */
    public static String getLatestPayTitleStr(Long time) {
        if(time == null) {
            return null;
        }

        String preStr = Utils.convertTimeToString(time, "yyyy年MM月dd日");

        if(preStr == null) {
            return null;
        }

        return preStr + "以后的最新还款记录";
    }

    /**
     *  根据标注及声明对象类型代码表获取 code 对应的 对象类型
     * @param code 标注及声明对象类型代码
     * @return 对象类型
     */
    public static String getAnnotationStatementObjTypeStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "1":
                return "报告";
            case "2":
                return "数据块";
            default:
                return code;
        }
    }

    /**
     *  根据标注及声明对象标识代码表获取 code 对应的 对象标识
     * @param code 标注及声明对象标识代码
     * @return 对象标识
     */
    public static String getAnnotationStatementObjStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "1":
                return "报告";
            case "201":
                return "身份信息";
            case "202":
                return "婚姻信息";
            case "203":
                return "居住信息";
            case "204":
                return "职业信息";
            default:
                return code;
        }
    }

    /**
     *  根据标注及声明类型代码表获取 code 对应的 标注类型
     * @param code 标注及声明类型代码
     * @return 标注类型
     */
    public static String getAnnotationStatementTypeStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "1":
                return "异议标注";
            case "2":
                return "特殊标注";
            case "3":
                return "个人声明";
            case "4":
                return "机构说明";
            case "5":
                return "征信中心说明";
            default:
                return code;
        }
    }

    /**
     *  根据民事判决信息段所含数据项获取 code 对应的 结案方式
     * @param code 代码
     * @return 结案方式
     */
    public static String getCJCaseCloseStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "1":
                return "判决";
            case "2":
                return "调解";
            case "3":
                return "其他";
            default:
                return code;
        }
    }

    /**
     *  根据强制执行记录信息段所含数据项获取 code 对应的 结案方式
     * @param code 代码
     * @return 结案方式
     */
    public static String getEnforceCaseCloseStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "001":
                return "不予执行";
            case "002":
                return "自动履行";
            case "003":
                return "和解履行完毕";
            case "004":
                return "执行完毕";
            case "005":
                return "终结执行";
            case "006":
                return "提级执行";
            case "007":
                return "指定执行";
            case "255":
                return "其他";
            default:
                return code;
        }
    }

    /**
     *  根据住房公积金参缴信息段所含数据项获取 code 对应的 缴费状态
     * @param code 代码
     * @return 缴费状态
     */
    public static String getHFPayStatueStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "1":
                return "缴存";
            case "2":
                return "封存";
            case "3":
                return "销户";
            default:
                return code;
        }
    }

    /**
     *  根据住房公积金参缴信息段所含数据项获取 code 对应的 缴费状态
     * @param code 代码
     * @return 缴费状态
     */
    public static String getHFPayStatue(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
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

    /** todo 有类似代码表
     *  根据后付费业务信息段所含数据项获取 code 对应的 业务类型
     * @param code 代码
     * @return 业务类型
     */
    public static String getPostPaidBusiTypeStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "1":
                return "固定电话";
            case "2":
                return "移动电话";
            case "3":
                return "互联网接入";
            case "4":
                return "数据专线及集群业务";
            case "5":
                return "卫星业务";
            case "6":
                return "组合业务";
            case "0":
                return "其他业务";
            default:
                return code;
        }
    }

    /**
     *  根据后付费业务信息段所含数据项获取 code 对应的 电信账户当前缴费状态
     * @param code 代码
     * @return 电信账户当前缴费状态
     */
    public static String getTEPostPaidStatusStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "0":
                return "欠费";
            case "1":
                return "正常";
            default:
                return code;
        }
    }

    /**
     *  根据后付费业务信息段所含数据项获取 code 对应的 电信账户最近24月缴费状态
     * @param code 代码
     * @return 电信账户最近24月缴费状态
     */
    public static String getTEPostPaid24MStatusStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "#":
                return "未知";
            case "*":
                return "服务已开通但本月不需缴费";
            case "N":
                return "正常";
            case "0":
                return "欠费超过宽限期不足1个月";
            case "1":
                return "欠费超过宽限期1个月不足2个月";
            case "2":
                return "欠费超过宽限期2个月不足3个月";
            case "3":
                return "欠费超过宽限期3个月不足4个月";
            case "4":
                return "欠费超过宽限期4个月不足5个月";
            case "5":
                return "欠费超过宽限期5个月不足6个月";
            case "6":
                return "欠费超过宽限期6个月以上";
            case "C":
                return "销户";
            case "G":
                return "结束";
            default:
                return code;
        }
    }

    /**
     * 根据低保救助户主的人员类别代码表获取 code 对应的 低保救助户主的人员类别
     * @param code 低保救助户主的人员类别代码
     * @return 低保救助户主的人员类别
     */
    public static String getLSRescueStaffClassStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "1":
                return "在职职工";
            case "2":
                return "离岗";
            case "3":
                return "失业";
            case "4":
                return "离退休人员";
            case "5":
                return "三无人员";
            case "6":
                return "居民";
            case "7":
                return "学生";
            default:
                return code;
        }
    }

    /**
     * 根据执业资格证书的等级代码表获取 code 对应的 执业资格证书的等级
     * @param code 执业资格证书的等级代码
     * @return 执业资格证书的等级
     */
    public static String getPracticeCerLevelStr(String code) {
        if(code == null) {
            return null;
        }

        switch (code) {
            case "1":
                return "国家级机构或行业协会颁发的执资格证书";
            case "2":
                return "省市级机构或行业协会颁发的执资格证书";
            case "3":
                return "地市级机构或行业协会颁发的执资格证书";
            case "4":
                return "独立行业协会或制订标准的企颁发执资格证书";
            case "5":
                return "其他机构颁发的执业资格证书";
            default:
                return code;
        }
    }

    /**
     * 通过带有约定特殊值的时间戳和日期格式获取日期
     * @param date 时间戳
     * @param pattern 日期格式
     * @return 日期
     */
    public static String getDateWithSpecialStr(Long date, String pattern) {
        if(date == null || pattern == null) {
            return null;
        }

        // TODO:     public static final Long XINGFU_SPCECIAL_DATE_LONG = -2209017600000L;// 约定的需要转为 0000-00 的特殊日期值
        Long specialDate = -2209017600000L;// 约定的需要转为 0000-00 的特殊日期值

//        if(Consts.XINGFU_SPCECIAL_DATE_LONG == date) {
        if(Objects.equals(specialDate, date)) {
            return "0000-00";
        }

        return Utils.convertTimeToString(date, pattern);
    }

    public static String getAccoTypeEnumStr(LoanAcctType accoType) {
        switch (accoType) {
            case NonrevolvingCredit:
                return "D1";
            case RevolvingCredit:
                return "R1";
            case CreditCard:
                return "R2";
            case QuasiCreditCard:
                return "R3";
            case SubAccountUnderRevolvingQuota:
                return "R4";
            default:
                return null;
        }
    }


}
