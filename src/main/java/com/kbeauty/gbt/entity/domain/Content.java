package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kbeauty.gbt.entity.enums.Active;
import com.kbeauty.gbt.entity.enums.ContentStatus;
import com.kbeauty.gbt.entity.enums.ContentType;
import com.kbeauty.gbt.entity.enums.ContentViewType;
import com.kbeauty.gbt.entity.view.ImageData;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity 
@Table(name = "CONTENT")
@Data
@EqualsAndHashCode(callSuper=false)
public class Content extends CommonDomain {
	
	@Id	
	private long seq                ;
	@Column(name = "contentid")	private String contentId       ;
	
	
	@Column(name = "contenttype")	private String contentType     ;
	@Column(name = "orders") private int orders;
	
	private String local           ;
	@Column(name = "ownerid")	private String ownerId         ;
	
	private String title           ;
	private String content         ;
	
	@Column(name = "viewtype") private String viewType;
	@Column(name = "replyyn") private String replyYn;

	
	private String latitude        ;
	private String longitude       ;
	@Column(name = "geoname")	private String geoName         ;
	@Column(name = "consumerid")	private String consumerId      ;
	private String ip              ;
	private String path            ;
	private int depth              ;
	private String active          ;
	private String status          ;
	
	@Column(name = "startdate")	private String startDate;
	@Column(name = "enddate")	private String endDate;
	
	
	@Column(name = "mainurl")	private String mainUrl;
	@Column(name = "mainlink")	private String mainLink;
	@Column(name = "maindir")	private String mainDir;
	@Column(name = "mainfilename")	private String mainFilename;
	@Transient private ImageData mainImgData;
	
	
	
	@Column(name = "category1")	private String category1;
	@Column(name = "category2")	private String category2;
	@Column(name = "category3")	private String category3;
	
	private String vendor          ;
	private String description     ;
	private String element     ;
	
	@Column(name = "reviewgrade")	private Float reviewGrade;
		

	
	@Transient private String upperContentId    ; //?????????????????? ?????? 
	@Transient private String ownerName         ; //User ???????????? ??????
	@Transient private String ownerImgUrl       ; //User ???????????? ??????
	@Transient private ImageData ownerImgData;
	
//Enum?????? ??????
	@Transient private String activeName     ;
	@Transient private String viewTypeName     ;
	@Transient private String contentTypeName     ;
	@Transient private String statusName     ;
	
//Db?????? ???????????? ??????
	@Transient private String category1Name     ;
	@Transient private String category2Name     ;
	@Transient private String category3Name     ;

//?????? ????????? ?????? ??????
	@Transient private float avgReviewGrade;
	
	@Transient private String priority     ;
	
	public void setEnumName() {
		activeName = CommonUtil.getValue(Active.values(), active);
		viewTypeName = CommonUtil.getValue(ContentViewType.values(), viewType);
		contentTypeName = CommonUtil.getValue(ContentType.values(), contentType);
		statusName = CommonUtil.getValue(ContentStatus.values(), status);
		
	}

	public boolean isMainImg() {
		return ! StringUtil.isEmpty(mainDir);
	}

	
	public boolean isProduct() {
		return ContentType.PRODUCT.getCode().equals(contentType);
	}
	
	public String getVendor() {
		if(StringUtil.isEmpty(vendor)) {
			return getOwnerName();
		}else {
			return vendor;
		}
	}
}
