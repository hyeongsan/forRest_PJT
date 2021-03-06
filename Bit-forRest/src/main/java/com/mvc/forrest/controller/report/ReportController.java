package com.mvc.forrest.controller.report;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mvc.forrest.service.chat.ChatService;
import com.mvc.forrest.service.domain.Old;
import com.mvc.forrest.service.domain.Report;
import com.mvc.forrest.service.old.OldService;
import com.mvc.forrest.service.report.ReportService;


@Controller
@RequestMapping("/report/*")
public class ReportController {

	@Autowired
	public ReportService reportService;
	
	@Autowired
	public OldService oldService;
	
	@Autowired
	public ChatService chatService;
	
	@GetMapping("addReport")
	public String addReport(
			Model model, @ModelAttribute("report") Report report ) throws Exception {
		
		//model.addAttribute(report);
		//System.out.println(report); // 신고된 유저, 신고 중고거래 게시물, 채팅방 번호 얻을 수 있어욤
		//System.out.println(chatService.getListChat(report.getReportChatroomNo())); // 모든 채팅내역 보실 수 있어욤
		 return null;
	}

	@PostMapping("addReport")
	public String addReport(@ModelAttribute("report") Report report, Model model) throws Exception {
		 System.out.println("POST : addReport");
		 System.out.println(report);

		 reportService.addReport(report);
		 System.out.println(report.getReportNo());
		
		 return "redirect:/";
	}
	
	@GetMapping("getReport")
	public String getReport(@RequestParam("reportNo") int reportNo, Model model ) throws Exception {
		Report report = reportService.getReport(reportNo);
		System.out.println(report);
		
		Old old= oldService.getOld(report.getReportOldNo());
		model.addAttribute("old", old);
		model.addAttribute("chatList", chatService.getListChat(report.getReportChatroomNo()));
		model.addAttribute("reportUser", report.getReportUser());

		return "report/getReport";
	}	
	
	@PostMapping("updateReportCode")
	public String updateReportCode(@ModelAttribute("report") Report report, Model model ) throws Exception {
		
		reportService.updateReportCode(report);
		
		return "report/addReport";
	}
	
	@RequestMapping("listReport")
	public String listReport(Model model) throws Exception {
		
		reportService.getReportList();
		model.addAttribute("reportList", reportService.getReportList());
		return "report/listReport";
	}		
	
	
}