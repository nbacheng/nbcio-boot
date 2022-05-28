package com.nbcio.modules.estar.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecg.common.aspect.annotation.Dict;

import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: oa_gzspb
 * @Author: nbacheng
 * @Date:   2022-02-14
 * @Version: V1.0
 */
@Data
@TableName("oa_gzspb")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="oa_gzspb对象", description="oa_gzspb")
public class OaGzspb implements Serializable {
    private static final long serialVersionUID = 1L;

    /**id*/
	//@TableId(type = IdType.ASSIGN_ID)
    //@ApiModelProperty(value = "id")
    //private java.lang.String id;
	/**empno*/
	@Excel(name = "empno", width = 15)
    @ApiModelProperty(value = "empno")
    private java.lang.String empno;
	/**depno*/
	@Excel(name = "depno", width = 15)
    @ApiModelProperty(value = "depno")
    private java.lang.String depno;
	/**salaryyear*/
	@Excel(name = "salaryyear", width = 15)
    @ApiModelProperty(value = "salaryyear")
    private java.lang.Integer salaryyear;
	/**salarymonth*/
	@Excel(name = "salarymonth", width = 15)
    @ApiModelProperty(value = "salarymonth")
    private java.lang.Integer salarymonth;
	/**qts*/
	@Excel(name = "qts", width = 15)
    @ApiModelProperty(value = "qts")
    private java.math.BigDecimal qts;
	/**shouldworkdays*/
	@Excel(name = "shouldworkdays", width = 15)
    @ApiModelProperty(value = "shouldworkdays")
    private java.math.BigDecimal shouldworkdays;
	/**factworkdays*/
	@Excel(name = "factworkdays", width = 15)
    @ApiModelProperty(value = "factworkdays")
    private java.math.BigDecimal factworkdays;
	/**absentworkdays*/
	@Excel(name = "absentworkdays", width = 15)
    @ApiModelProperty(value = "absentworkdays")
    private java.math.BigDecimal absentworkdays;
	/**middleworkdays*/
	@Excel(name = "middleworkdays", width = 15)
    @ApiModelProperty(value = "middleworkdays")
    private java.math.BigDecimal middleworkdays;
	/**nightworkdays*/
	@Excel(name = "nightworkdays", width = 15)
    @ApiModelProperty(value = "nightworkdays")
    private java.math.BigDecimal nightworkdays;
	/**lates*/
	@Excel(name = "lates", width = 15)
    @ApiModelProperty(value = "lates")
    private java.math.BigDecimal lates;
	/**overtimehours*/
	@Excel(name = "overtimehours", width = 15)
    @ApiModelProperty(value = "overtimehours")
    private java.math.BigDecimal overtimehours;
	/**overtimedays*/
	@Excel(name = "overtimedays", width = 15)
    @ApiModelProperty(value = "overtimedays")
    private java.math.BigDecimal overtimedays;
	/**qjb*/
	@Excel(name = "qjb", width = 15)
    @ApiModelProperty(value = "qjb")
    private java.math.BigDecimal qjb;
	/**absentdays*/
	@Excel(name = "absentdays", width = 15)
    @ApiModelProperty(value = "absentdays")
    private java.math.BigDecimal absentdays;
	/**latetime*/
	@Excel(name = "latetime", width = 15)
    @ApiModelProperty(value = "latetime")
    private java.lang.Integer latetime;
	/**sj*/
	@Excel(name = "sj", width = 15)
    @ApiModelProperty(value = "sj")
    private java.math.BigDecimal sj;
	/**bj*/
	@Excel(name = "bj", width = 15)
    @ApiModelProperty(value = "bj")
    private java.math.BigDecimal bj;
	/**cj*/
	@Excel(name = "cj", width = 15)
    @ApiModelProperty(value = "cj")
    private java.math.BigDecimal cj;
	/**gsj*/
	@Excel(name = "gsj", width = 15)
    @ApiModelProperty(value = "gsj")
    private java.math.BigDecimal gsj;
	/**hsj*/
	@Excel(name = "hsj", width = 15)
    @ApiModelProperty(value = "hsj")
    private java.math.BigDecimal hsj;
	/**dxnxj*/
	@Excel(name = "dxnxj", width = 15)
    @ApiModelProperty(value = "dxnxj")
    private java.math.BigDecimal dxnxj;
	/**wpznxj*/
	@Excel(name = "wpznxj", width = 15)
    @ApiModelProperty(value = "wpznxj")
    private java.math.BigDecimal wpznxj;
	/**basepay*/
	@Excel(name = "basepay", width = 15)
    @ApiModelProperty(value = "basepay")
    private java.math.BigDecimal basepay;
	/**skillpay*/
	@Excel(name = "skillpay", width = 15)
    @ApiModelProperty(value = "skillpay")
    private java.math.BigDecimal skillpay;
	/**positionpay*/
	@Excel(name = "positionpay", width = 15)
    @ApiModelProperty(value = "positionpay")
    private java.math.BigDecimal positionpay;
	/**addpay*/
	@Excel(name = "addpay", width = 15)
    @ApiModelProperty(value = "addpay")
    private java.math.BigDecimal addpay;
	/**jbf*/
	@Excel(name = "jbf", width = 15)
    @ApiModelProperty(value = "jbf")
    private java.math.BigDecimal jbf;
	/**subconfficient*/
	@Excel(name = "subconfficient", width = 15)
    @ApiModelProperty(value = "subconfficient")
    private java.math.BigDecimal subconfficient;
	/**sumpay*/
	@Excel(name = "sumpay", width = 15)
    @ApiModelProperty(value = "sumpay")
    private java.math.BigDecimal sumpay;
	/**ykgz*/
	@Excel(name = "ykgz", width = 15)
    @ApiModelProperty(value = "ykgz")
    private java.math.BigDecimal ykgz;
	/**bkgz*/
	@Excel(name = "bkgz", width = 15)
    @ApiModelProperty(value = "bkgz")
    private java.math.BigDecimal bkgz;
	/**yfgz*/
	@Excel(name = "yfgz", width = 15)
    @ApiModelProperty(value = "yfgz")
    private java.math.BigDecimal yfgz;
	/**nightallowance*/
	@Excel(name = "nightallowance", width = 15)
    @ApiModelProperty(value = "nightallowance")
    private java.math.BigDecimal nightallowance;
	/**zfbt*/
	@Excel(name = "zfbt", width = 15)
    @ApiModelProperty(value = "zfbt")
    private java.math.BigDecimal zfbt;
	/**treatment*/
	@Excel(name = "treatment", width = 15)
    @ApiModelProperty(value = "treatment")
    private java.math.BigDecimal treatment;
	/**tcbt*/
	@Excel(name = "tcbt", width = 15)
    @ApiModelProperty(value = "tcbt")
    private java.math.BigDecimal tcbt;
	/**lyf*/
	@Excel(name = "lyf", width = 15)
    @ApiModelProperty(value = "lyf")
    private java.math.BigDecimal lyf;
	/**nxjbt*/
	@Excel(name = "nxjbt", width = 15)
    @ApiModelProperty(value = "nxjbt")
    private java.math.BigDecimal nxjbt;
	/**gwjt*/
	@Excel(name = "gwjt", width = 15)
    @ApiModelProperty(value = "gwjt")
    private java.math.BigDecimal gwjt;
	/**zybbt*/
	@Excel(name = "zybbt", width = 15)
    @ApiModelProperty(value = "zybbt")
    private java.math.BigDecimal zybbt;
	/**bk*/
	@Excel(name = "bk", width = 15)
    @ApiModelProperty(value = "bk")
    private java.math.BigDecimal bk;
	/**qtbt*/
	@Excel(name = "qtbt", width = 15)
    @ApiModelProperty(value = "qtbt")
    private java.math.BigDecimal qtbt;
	/**allowance*/
	@Excel(name = "allowance", width = 15)
    @ApiModelProperty(value = "allowance")
    private java.math.BigDecimal allowance;
	/**rewards*/
	@Excel(name = "rewards", width = 15)
    @ApiModelProperty(value = "rewards")
    private java.math.BigDecimal rewards;
	/**punishment*/
	@Excel(name = "punishment", width = 15)
    @ApiModelProperty(value = "punishment")
    private java.math.BigDecimal punishment;
	/**wholeaward*/
	@Excel(name = "wholeaward", width = 15)
    @ApiModelProperty(value = "wholeaward")
    private java.math.BigDecimal wholeaward;
	/**sumrp*/
	@Excel(name = "sumrp", width = 15)
    @ApiModelProperty(value = "sumrp")
    private java.math.BigDecimal sumrp;
	/**ybkk*/
	@Excel(name = "ybkk", width = 15)
    @ApiModelProperty(value = "ybkk")
    private java.math.BigDecimal ybkk;
	/**zsf*/
	@Excel(name = "zsf", width = 15)
    @ApiModelProperty(value = "zsf")
    private java.math.BigDecimal zsf;
	/**txf*/
	@Excel(name = "txf", width = 15)
    @ApiModelProperty(value = "txf")
    private java.math.BigDecimal txf;
	/**gzzyj*/
	@Excel(name = "gzzyj", width = 15)
    @ApiModelProperty(value = "gzzyj")
    private java.math.BigDecimal gzzyj;
	/**wyj*/
	@Excel(name = "wyj", width = 15)
    @ApiModelProperty(value = "wyj")
    private java.math.BigDecimal wyj;
	/**kjk*/
	@Excel(name = "kjk", width = 15)
    @ApiModelProperty(value = "kjk")
    private java.math.BigDecimal kjk;
	/**dkkx*/
	@Excel(name = "dkkx", width = 15)
    @ApiModelProperty(value = "dkkx")
    private java.math.BigDecimal dkkx;
	/**ylbxjs*/
	@Excel(name = "ylbxjs", width = 15)
    @ApiModelProperty(value = "ylbxjs")
    private java.math.BigDecimal ylbxjs;
	/**ylbx*/
	@Excel(name = "ylbx", width = 15)
    @ApiModelProperty(value = "ylbx")
    private java.math.BigDecimal ylbx;
	/**sybx*/
	@Excel(name = "sybx", width = 15)
    @ApiModelProperty(value = "sybx")
    private java.math.BigDecimal sybx;
	/**zfgjj*/
	@Excel(name = "zfgjj", width = 15)
    @ApiModelProperty(value = "zfgjj")
    private java.math.BigDecimal zfgjj;
	/**dkbx*/
	@Excel(name = "dkbx", width = 15)
    @ApiModelProperty(value = "dkbx")
    private java.math.BigDecimal dkbx;
	/**tax*/
	@Excel(name = "tax", width = 15)
    @ApiModelProperty(value = "tax")
    private java.math.BigDecimal tax;
	/**toyalpay*/
	@Excel(name = "toyalpay", width = 15)
    @ApiModelProperty(value = "toyalpay")
    private java.math.BigDecimal toyalpay;
	/**grgz*/
	@Excel(name = "grgz", width = 15)
    @ApiModelProperty(value = "grgz")
    private java.math.BigDecimal grgz;
	/**gzzh*/
	@Excel(name = "gzzh", width = 15)
    @ApiModelProperty(value = "gzzh")
    private java.lang.String gzzh;
	/**xj*/
	@Excel(name = "xj", width = 15)
    @ApiModelProperty(value = "xj")
    private java.lang.String xj;
	/**zj*/
	@Excel(name = "zj", width = 15)
    @ApiModelProperty(value = "zj")
    private java.lang.String zj;
	/**bak7*/
	@Excel(name = "bak7", width = 15)
    @ApiModelProperty(value = "bak7")
    private java.lang.String bak7;
	/**hzj*/
	@Excel(name = "hzj", width = 15)
    @ApiModelProperty(value = "hzj")
    private java.lang.String hzj;
	/**hxj*/
	@Excel(name = "hxj", width = 15)
    @ApiModelProperty(value = "hxj")
    private java.lang.String hxj;
	/**gzkhbak*/
	@Excel(name = "gzkhbak", width = 15)
    @ApiModelProperty(value = "gzkhbak")
    private java.lang.String gzkhbak;
	/**khhjngz*/
	@Excel(name = "khhjngz", width = 15)
    @ApiModelProperty(value = "khhjngz")
    private java.math.BigDecimal khhjngz;
	/**ylbxgs*/
	@Excel(name = "ylbxgs", width = 15)
    @ApiModelProperty(value = "ylbxgs")
    private java.math.BigDecimal ylbxgs;
	/**sybxgs*/
	@Excel(name = "sybxgs", width = 15)
    @ApiModelProperty(value = "sybxgs")
    private java.math.BigDecimal sybxgs;
	/**zfgjjgs*/
	@Excel(name = "zfgjjgs", width = 15)
    @ApiModelProperty(value = "zfgjjgs")
    private java.math.BigDecimal zfgjjgs;
	/**jjzj*/
	@Excel(name = "jjzj", width = 15)
    @ApiModelProperty(value = "jjzj")
    private java.lang.String jjzj;
	/**jjxj*/
	@Excel(name = "jjxj", width = 15)
    @ApiModelProperty(value = "jjxj")
    private java.lang.String jjxj;
	/**lastjjxj*/
	@Excel(name = "lastjjxj", width = 15)
    @ApiModelProperty(value = "lastjjxj")
    private java.lang.String lastjjxj;
	/**bonuscoefficient*/
	@Excel(name = "bonuscoefficient", width = 15)
    @ApiModelProperty(value = "bonuscoefficient")
    private java.math.BigDecimal bonuscoefficient;
	/**bonus*/
	@Excel(name = "bonus", width = 15)
    @ApiModelProperty(value = "bonus")
    private java.math.BigDecimal bonus;
	/**ydjj*/
	@Excel(name = "ydjj", width = 15)
    @ApiModelProperty(value = "ydjj")
    private java.math.BigDecimal ydjj;
	/**jdjj*/
	@Excel(name = "jdjj", width = 15)
    @ApiModelProperty(value = "jdjj")
    private java.math.BigDecimal jdjj;
	/**jjze*/
	@Excel(name = "jjze", width = 15)
    @ApiModelProperty(value = "jjze")
    private java.math.BigDecimal jjze;
	/**bkjj*/
	@Excel(name = "bkjj", width = 15)
    @ApiModelProperty(value = "bkjj")
    private java.math.BigDecimal bkjj;
	/**ykjj*/
	@Excel(name = "ykjj", width = 15)
    @ApiModelProperty(value = "ykjj")
    private java.math.BigDecimal ykjj;
	/**khhjjxs*/
	@Excel(name = "khhjjxs", width = 15)
    @ApiModelProperty(value = "khhjjxs")
    private java.math.BigDecimal khhjjxs;
	/**jjkhbak*/
	@Excel(name = "jjkhbak", width = 15)
    @ApiModelProperty(value = "jjkhbak")
    private java.lang.String jjkhbak;
	/**totalbonus*/
	@Excel(name = "totalbonus", width = 15)
    @ApiModelProperty(value = "totalbonus")
    private java.math.BigDecimal totalbonus;
	/**jjzh*/
	@Excel(name = "jjzh", width = 15)
    @ApiModelProperty(value = "jjzh")
    private java.lang.String jjzh;
	/**xzze*/
	@Excel(name = "xzze", width = 15)
    @ApiModelProperty(value = "xzze")
    private java.lang.Integer xzze;
	/**ykxz*/
	@Excel(name = "ykxz", width = 15)
    @ApiModelProperty(value = "ykxz")
    private java.lang.Integer ykxz;
	/**bkxz*/
	@Excel(name = "bkxz", width = 15)
    @ApiModelProperty(value = "bkxz")
    private java.lang.Integer bkxz;
	/**sfxz*/
	@Excel(name = "sfxz", width = 15)
    @ApiModelProperty(value = "sfxz")
    private java.lang.Integer sfxz;
	/**sumpay1*/
	@Excel(name = "sumpay1", width = 15)
    @ApiModelProperty(value = "sumpay1")
    private java.math.BigDecimal sumpay1;
	/**sumpay2*/
	@Excel(name = "sumpay2", width = 15)
    @ApiModelProperty(value = "sumpay2")
    private java.math.BigDecimal sumpay2;
	/**yfgz1*/
	@Excel(name = "yfgz1", width = 15)
    @ApiModelProperty(value = "yfgz1")
    private java.math.BigDecimal yfgz1;
	/**yfgz2*/
	@Excel(name = "yfgz2", width = 15)
    @ApiModelProperty(value = "yfgz2")
    private java.math.BigDecimal yfgz2;
	/**ykgz1*/
	@Excel(name = "ykgz1", width = 15)
    @ApiModelProperty(value = "ykgz1")
    private java.math.BigDecimal ykgz1;
	/**ykgz2*/
	@Excel(name = "ykgz2", width = 15)
    @ApiModelProperty(value = "ykgz2")
    private java.math.BigDecimal ykgz2;
	/**toyalpay1*/
	@Excel(name = "toyalpay1", width = 15)
    @ApiModelProperty(value = "toyalpay1")
    private java.math.BigDecimal toyalpay1;
	/**toyalpay2*/
	@Excel(name = "toyalpay2", width = 15)
    @ApiModelProperty(value = "toyalpay2")
    private java.math.BigDecimal toyalpay2;
	/**comsubsidy*/
	@Excel(name = "comsubsidy", width = 15)
    @ApiModelProperty(value = "comsubsidy")
    private java.math.BigDecimal comsubsidy;
	/**corpsubsidy*/
	@Excel(name = "corpsubsidy", width = 15)
    @ApiModelProperty(value = "corpsubsidy")
    private java.math.BigDecimal corpsubsidy;
	/**kshbt*/
	@Excel(name = "kshbt", width = 15)
    @ApiModelProperty(value = "kshbt")
    private java.math.BigDecimal kshbt;
	/**allowancenew*/
	@Excel(name = "allowancenew", width = 15)
    @ApiModelProperty(value = "allowancenew")
    private java.math.BigDecimal allowancenew;
	/**tax1*/
	@Excel(name = "tax1", width = 15)
    @ApiModelProperty(value = "tax1")
    private java.math.BigDecimal tax1;
	/**tax2*/
	@Excel(name = "tax2", width = 15)
    @ApiModelProperty(value = "tax2")
    private java.math.BigDecimal tax2;
	/**yilbx*/
	@Excel(name = "yilbx", width = 15)
    @ApiModelProperty(value = "yilbx")
    private java.math.BigDecimal yilbx;
	/**yilbxgs*/
	@Excel(name = "yilbxgs", width = 15)
    @ApiModelProperty(value = "yilbxgs")
    private java.math.BigDecimal yilbxgs;
	/**bak9*/
	@Excel(name = "bak9", width = 15)
    @ApiModelProperty(value = "bak9")
    private java.lang.String bak9;
	/**bak10*/
	@Excel(name = "bak10", width = 15)
    @ApiModelProperty(value = "bak10")
    private java.lang.String bak10;
	/**bak8*/
	@Excel(name = "bak8", width = 15)
    @ApiModelProperty(value = "bak8")
    private java.lang.String bak8;
	/**taxfj1*/
	@Excel(name = "taxfj1", width = 15)
    @ApiModelProperty(value = "taxfj1")
    private java.math.BigDecimal taxfj1;
	/**taxfj2*/
	@Excel(name = "taxfj2", width = 15)
    @ApiModelProperty(value = "taxfj2")
    private java.math.BigDecimal taxfj2;
	/**taxfj3*/
	@Excel(name = "taxfj3", width = 15)
    @ApiModelProperty(value = "taxfj3")
    private java.math.BigDecimal taxfj3;
	/**taxfj4*/
	@Excel(name = "taxfj4", width = 15)
    @ApiModelProperty(value = "taxfj4")
    private java.math.BigDecimal taxfj4;
	/**taxfj5*/
	@Excel(name = "taxfj5", width = 15)
    @ApiModelProperty(value = "taxfj5")
    private java.math.BigDecimal taxfj5;
	/**taxyj*/
	@Excel(name = "taxyj", width = 15)
    @ApiModelProperty(value = "taxyj")
    private java.math.BigDecimal taxyj;
	/**ym*/
	@Excel(name = "ym", width = 15)
    @ApiModelProperty(value = "ym")
    private java.lang.Integer ym;
	/**taxmonth*/
	@Excel(name = "taxmonth", width = 15)
    @ApiModelProperty(value = "taxmonth")
    private java.lang.Integer taxmonth;
	/**registerdate*/
	@Excel(name = "registerdate", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "registerdate")
    private java.util.Date registerdate;
	/**username*/
	@Excel(name = "username", width = 15)
    @ApiModelProperty(value = "username")
    private java.lang.String username;
	/**depname*/
	@Excel(name = "depname", width = 15)
    @ApiModelProperty(value = "depname")
    private java.lang.String depname;
}
