package com.kbeauty.gbt.controller;

import com.google.api.Http;
import com.kbeauty.gbt.entity.Paging;
import com.kbeauty.gbt.entity.PagingList;
import com.kbeauty.gbt.entity.domain.*;
import com.kbeauty.gbt.entity.enums.*;
import com.kbeauty.gbt.entity.view.*;
import com.kbeauty.gbt.service.ContentService;
import com.kbeauty.gbt.service.RecruitService;
import com.kbeauty.gbt.util.FileUtil;
import com.kbeauty.gbt.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/w1/recruit")
@Controller
@Slf4j
public class RecruitCtrl {

	@Autowired
	RecruitService service;

	@Resource
	private Login login;

	private final static String conditionKey = "RecruitCondition";
//	바뀔 일 없는 이름이라 고정함
private final static String conditionByKey = "RecruitByCondition";

	private void setCondition(HttpServletRequest request, RecruitCondition condition) {
		//TODO 기존에 conditionKey를 다 삭제한다.
		HttpSession session = request.getSession();
		session.setAttribute(conditionKey, condition);
	}

	private void setByCondition(HttpServletRequest request, RecruitByCondition condition) {
		//TODO 기존에 conditionKey를 다 삭제한다.
		HttpSession session = request.getSession();
		session.setAttribute(conditionByKey, condition);
	}


	private RecruitCondition getCondition(HttpServletRequest request) {
		HttpSession session = request.getSession();
		RecruitCondition condiiton = (RecruitCondition)session.getAttribute(conditionKey);
		return condiiton;
	}

	private RecruitByCondition getPremiumCondition(HttpServletRequest request) {
		HttpSession session = request.getSession();
		RecruitByCondition premiumCondiiton = (RecruitByCondition)session.getAttribute(conditionByKey);
		return premiumCondiiton;
	}

	@GetMapping("/go_list")
	public String goList(HttpServletRequest request, RecruitCondition condition, Model model) {

		String reset = request.getParameter("reset");
		if(YesNo.isYes(reset)) {
			condition = new RecruitCondition();
			condition.setRecruitType(RecruitType.EVENT.getCode());
		}else {
			if(condition == null || condition.isEmpty()) {
				condition = getCondition(request);
				if(condition == null) {
					condition = new RecruitCondition();
					condition.setRecruitType(RecruitType.EVENT.getCode());
				}
			}
		}


		model.addAttribute("condition", condition);

		return "admin/recruit/list";
	}


	@RequestMapping("/premium/go_list")
	public String list(HttpServletRequest request, RecruitByCondition condition, Model model) {


		String reset = request.getParameter("reset");
		if (YesNo.isYes(reset)) {
			condition = new RecruitByCondition();
		} else {
			if (condition == null || condition.isEmpty()) {
				condition = getPremiumCondition(request);
				if(condition == null) {
					condition = new RecruitByCondition();
				}
			}

			model.addAttribute("condition", condition);

		}
		return "admin/recruit/premium_list";
	}

	// TODO:추가
	@RequestMapping(value="/premium_list")
	@ResponseBody
	public PagingList<RecruitByView> getRecruitListView(HttpServletRequest request, @RequestBody RecruitByCondition condition) {

		PagingList<RecruitByView> list = new PagingList<>();
		if(condition == null)
			condition = new RecruitByCondition();

		Paging paging = new Paging();
		paging.setCondition(condition);

		int currPage = condition.getPage();
		if(currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);
		setByCondition(request, condition);
//

		List<RecruitByView> recruitByViewList = service.getPremiumList(condition);
		if(recruitByViewList == null || recruitByViewList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);
			return list;
		}

		int totalCnt = service.getPremiumByListCnt(condition);
		paging.setTotalCount(totalCnt);

		list.setList(recruitByViewList);
		list.setPaging(paging);
		list.setOk();
		return list;
	}


	@GetMapping("/go_create")
	public String goCreate() {
		return "admin/recruit/create";
	}


	@RequestMapping(value="/create", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	public String create(Recruit recruit,
						 @RequestParam("mainUploadFile") MultipartFile mainFile,
						 @RequestParam("fileName") List<MultipartFile> files,
						 @RequestParam("resourceType") List<String> resourceType,
						 @RequestParam("resourceTitle") List<String> resourceTitle,
						 @RequestParam("resourceContent") List<String> resourceContent,
						 @RequestParam("url") List<String> url,
						 Model model
			) throws Exception{

		List<Resources> list = new ArrayList<>();
//		Resources res = null;
//		
//		for (MultipartFile file : files) {
//			log.debug(file.getName());
//		}

		String userId = login.getUserId();

		String recruitId = service.getRecruitId();
		long recruitSeq = service.getRecruitSeq();
		recruit.setRecruitId(recruitId);
		recruit.setSeq(recruitSeq);
		recruit.setUserId(userId);


		RecruitView view = new RecruitView();
		view.setRecruit(recruit);
		view.setResourceList(list);

		service.create(view);

		String nextPage = "redirect:/w1/recruit/detail/" + recruitId;

		return nextPage;
	}


	@RequestMapping(value="/list")
	@ResponseBody
	public PagingList<RecruitView> getRecruitListView(HttpServletRequest request, @RequestBody RecruitCondition condition) {

		PagingList<RecruitView> list = new PagingList<>();
		if(condition == null) condition = new RecruitCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);

		int currPage = condition.getPage();
		if(currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);

		setCondition(request, condition);

		String userId = login.getUserId();
		condition.setSearchUserid(userId);

		List<RecruitView> contentList = service.getRecruitViewListNotAnother(condition);
		if(contentList == null || contentList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);
			return list;
		}

		int totalCnt = service.getRecruitListCnt(condition);
		paging.setTotalCount(totalCnt);

		list.setList(contentList);
		list.setPaging(paging);
		list.setOk();
		return list;
	}




	@RequestMapping(value="/detail/{recruitId}", method=RequestMethod.GET)
	public String getRecruitView(
			@PathVariable("recruitId") String recruitId,
			Model model
			) {

		String userId = login.getUserId();

		RecruitView recruitView = service.getRecruitView(recruitId, userId);
		if(recruitView == null) {
			recruitView = new RecruitView();
			recruitView.setError(ErrMsg.NO_RESULT);
			return "";
		}
		recruitView.setOk();

		model.addAttribute("recruitView", recruitView);

		return "admin/recruit/detail";
	}

	@RequestMapping(value="/save_recruit", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	public String save(Recruit recruit) throws Exception{

		if(recruit == null) {
			recruit.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return "error";
		}
//		recruit가 null이면 NOSAVEERR

		String RecruitId = recruit.getRecruitId();

		if(StringUtil.isEmpty(RecruitId)) {
			recruit.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return "error";
		}

		String userId = login.getUserId();

		Recruit dbRecruit = service.getRecruit(RecruitId);
		dbRecruit.setRecruitType (recruit.getRecruitType ());
		dbRecruit.setTitle       (recruit.getTitle       ());
		dbRecruit.setContent     (recruit.getContent     ());
		dbRecruit.setStartDate   (recruit.getStartDate   ());
		dbRecruit.setEndDate     (recruit.getEndDate     ());
 		dbRecruit.setActive      (recruit.getActive      ());
		dbRecruit.setStatus      (recruit.getStatus      ());

		recruit = service.saveRecruit(dbRecruit, userId);


		recruit.setOk();

		String nextPage = "redirect:/w1/recruit/detail/" + recruit.getRecruitId();
		return nextPage;
	}



	@RequestMapping(value="/delete_recruit", method=RequestMethod.POST)
	public String delete(Recruit recruit){

		if(recruit == null) {
			recruit.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return "error";
		}
		String recruitId = recruit.getRecruitId();

		if(StringUtil.isEmpty(recruitId)) {
			recruit.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return "error";
		}

		String userId = login.getUserId();
		recruit = service.deleteRecruit(recruit, userId);
		recruit.setOk();

		String nextPage = "redirect:/w1/recruit/go_list";
		return nextPage;
	}



}
