package com.mvc.forrest.controller.user;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mvc.forrest.common.utils.FileNameUtils;
import com.mvc.forrest.config.auth.LoginUser;
import com.mvc.forrest.service.coupon.CouponService;
import com.mvc.forrest.service.domain.Coupon;
import com.mvc.forrest.service.domain.Old;
import com.mvc.forrest.service.domain.OldReview;
import com.mvc.forrest.service.domain.OwnCoupon;
import com.mvc.forrest.service.domain.Page;
import com.mvc.forrest.service.domain.Search;
import com.mvc.forrest.service.domain.User;
import com.mvc.forrest.service.old.OldService;
import com.mvc.forrest.service.oldreview.OldReviewService;
import com.mvc.forrest.service.user.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user/*")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private CouponService couponService;
	@Autowired
	private OldService oldService;
	@Autowired
	private OldReviewService oldReviewService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Value("5")
	int pageUnit;
	@Value("10")
	int pageSize;
	
	
	@GetMapping("login")			//??????, ?????????
	public String login() throws Exception{
		
		System.out.println("/user/login : GET");

		return "user/login";
	}
	
	
	@RequestMapping("afterLogin")
	public String afterLogin() throws Exception{
		
		System.out.println("/user/afterLogin");
		
		LoginUser sessionUser= (LoginUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = userService.getUser(sessionUser.getUser().getUserId());
		
		try {
        	System.out.println(":: Connect to Chatting Service");
		String reqURL = "http://192.168.0.42:3001/sessionLoginLogout/login/"+user.getUserId();
		URL url = new URL(reqURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(500);
		int responseCode = conn.getResponseCode();
		System.out.println(":: Chatting Service responseCode : " + responseCode);
        	System.out.println("Node server is Dead ..");
	}catch(Exception e){
		System.out.println("???????????? ??????");
	}
	
	try {
        if(user.getJoinDate().equals(user.getRecentDate())) {
	        OwnCoupon oc = new OwnCoupon();
			Coupon coupon = couponService.getCoupon("2");	//2??? ?????? = ???????????? ??????
			Calendar cal= Calendar.getInstance();
			cal.add(Calendar.DATE,30);
			Timestamp ts1 = new Timestamp(System.currentTimeMillis());
			Timestamp ts2 = new Timestamp(cal.getTimeInMillis());
			oc.setOwnUser(user);
			oc.setOwnCoupon(coupon);
			oc.setOwnCouponCreDate(ts1);
			oc.setOwnCouponDelDate(ts2);
			couponService.addOwnCoupon(oc);
			
			System.out.println("???????????? ????????????");
        }
	}catch(Exception e) {
//		e.printStackTrace();
		System.out.println("???????????? ???????????? ??????");
	}
	
    try {
    	System.out.println(user);
		System.out.println(user.getUserId());
		userService.updateRecentDate(user);
		
	} catch (Exception e) {
//		e.printStackTrace();
		System.out.println("???????????? ?????? ??????");
	}	

		
        return "redirect:/";
	}
	
	

//		### spring security ???????????? ?????? ????????? method	###
//	@RequestMapping("afterLogout")				//??????, ?????????
//	public String afterLogout(HttpServletRequest request, HttpServletResponse response) throws Exception{
//		
//			System.out.println("user/afterLogout : POST/GET");
//			
//				String reqURL = "https://kauth.kakao.com/oauth/logout?client_id=14488329bb0ccdf08f6b761a0726ab5a&logout_redirect_uri=http://localhost:8080/";
//				URL url = new URL(reqURL);
//				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//				conn.setRequestMethod("GET");
//				conn.setDoOutput(true);
//				int responseCode = conn.getResponseCode();
//				System.out.println("responseCode : " + responseCode);
//	        
//	        return "redirect:/";
//	}
	
	@GetMapping("addUser")				//??????, ?????????
	public String addUser() throws Exception{
		
		System.out.println("/user/addUser : GET");
		
		return "user/addUserView";
	}
	
	@RequestMapping("addUser")			//??????, ?????????
	public String addUser( @ModelAttribute("user") User user,
							@RequestParam("userImgFile")MultipartFile file ) throws Exception {

//		String temDir = "C:\\Users\\bitcamp\\git\\forRest\\Bit-forRest\\src\\main\\resources\\static\\images\\uploadFiles";
		String temDir = "C:\\Users\\lsm45\\git\\forRest\\Bit-forRest\\bin\\main\\static\\images\\uploadFiles";

		
		System.out.println("/user/addUser : POST");
		
		if (!file.getOriginalFilename().isEmpty()) {
            String filename = file.getOriginalFilename();
            filename =  FileNameUtils.fileNameConvert(filename);
            file.transferTo(new File(temDir,filename));
            user.setUserImg(filename);
        }
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		userService.addUser(user);
				
		return "user/login";
	}
	
	@GetMapping("findId")				//??????, ?????????
	public String findId () throws Exception{

		System.out.println("/user/findId : GET");
		
		return "user/findIdView";
	}
	
	@PostMapping("findId")				//??????, ?????????
	public String findId (@ModelAttribute("user") User user, String sms,
							Model model) throws Exception{
		System.out.println("/user/findId : POST");
		
		// sms ???????????? ?????? sms??? ??????sms??? ???????????????
		User userByPhone = userService.getUserByPhone(user.getPhone());
		User userByName = userService.getUserByName(user.getUserName());
		if(userByName.getUserId().equals(userByPhone.getUserId())){
			user = userByName;
			userByName.getJoinDate().toString().substring(pageUnit, pageSize);
			model.addAttribute("userId", user.getUserId());
			model.addAttribute("userJoinDate", user.getJoinDate().toString().substring(0, 10));
			
			return "user/findId";
		}
		 
		return "user/findIdView";
	}
	
	@GetMapping("findPwd")				//??????, ?????????
	public String findPwd() throws Exception{
		
		System.out.println("/user/findPwd : GET");
		
		return "user/findPwd";
	}
	
	@PostMapping("findPwd")				//??????, ?????????
	public String findPwd(@ModelAttribute("user") User user, String sms, 
							HttpSession session, Model model) throws Exception{
		
		System.out.println("/user/findPwd : POST");
		
		// sms?????? ??????
		
		User userById = userService.getUser(user.getUserId());
		User userByPhone = userService.getUserByPhone(user.getPhone());
		
		if(userById.getUserId().equals(userByPhone.getUserId())){
			session.setAttribute("user", userById);
			model.addAttribute("user", userById);
			return "user/pwdReset";
		}
		
		return "user/findPwd";
	}	
	
	@GetMapping("pwdReset")				//??????, ?????????				
	public String pwdReset() throws Exception{
		
		System.out.println("/user/pwdReset : GET");
		
		return "user/pwdReset";
	}
	
	@PostMapping("pwdReset")			//??????, ?????????
	public String pwdReset(@RequestParam("password") String password, HttpSession session) throws Exception{
		
		System.out.println("/user/pwdReset : POST");
		
		User sessionUser = (User)session.getAttribute("user");
		sessionUser.setPassword(password);
		userService.updatePassword(sessionUser);
		
		return "redirect:/";
	}
	
	@RequestMapping("getUserList")		//?????????
	public String getUserList( @ModelAttribute("search") Search search , Model model ) throws Exception{
		
		System.out.println("/user/getUserList : GET / POST");
		
		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		Map<String , Object> map=userService.getUserList(search);
		
		System.out.println("# map : "+map);
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		
		// Model ??? View ??????
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		
		return "user/getUserList";
	}
	
	@RequestMapping("getUser")		//??????, ?????????
	public String getUser( @RequestParam("userId") String userId , Model model,
							HttpSession session, Search search) throws Exception {
		
		System.out.println("/user/getUser : POST / GET");
		
		User dbUser = userService.getUser(userId);
		List<OldReview>oldReviewList = oldReviewService.getOldReviewList(userId);
		List<Old> oldList = oldService.getOldListForUser(dbUser.getUserId());
		
		for(int i=0; i<oldReviewList.size();i++) {
			oldReviewList.get(i).setOld(oldService.getOld(oldReviewList.get(i).getOld().getOldNo()));
			oldReviewList.get(i).setReviewUser(userService.getUser(oldReviewList.get(i).getReviewUser().getUserId()));
			oldReviewList.get(i).setUserRate(oldReviewService.getUserRate(oldReviewList.get(i).getReviewedUser().getUserId()));
		}
		
		model.addAttribute("oldList",oldList);
		model.addAttribute("oldReviewList", oldReviewList);
		model.addAttribute("user", dbUser);
		
		System.out.println("oldList : "+ oldList);
		System.out.println("oldReviewList : "+ oldReviewList);
		System.out.println("dbUser : "+dbUser);
		
		return "user/getUser";
	}
	
	@GetMapping("updateUser")
	public String updateUser( @RequestParam("userId") String userId , Model model ) throws Exception {
		
		System.out.println("/user/updateUser : GET");
		
		User user = userService.getUser(userId);
		
		model.addAttribute("user", user);
		
		return "user/updateUser";
	}
	
	@PostMapping("updateUser")			//??????, ?????????
	public String updateUser( @ModelAttribute("user") User user,
							@RequestParam("userImgFile")MultipartFile file) throws Exception {
		System.out.println("/user/updateUser : POST");

//		String temDir = "C:\\Users\\bitcamp\\git\\forRest\\Bit-forRest\\src\\main\\resources\\static\\images\\uploadFiles";
		String temDir = "C:\\Users\\lsm45\\git\\forRest\\Bit-forRest\\bin\\main\\static\\images\\uploadFiles";

		
		if (!file.getOriginalFilename().isEmpty()) {
            String filename = file.getOriginalFilename();
            filename =  FileNameUtils.fileNameConvert(filename);
            file.transferTo(new File(temDir,filename));
            user.setUserImg(filename);

        }
		userService.updateUser(user);
		///////////////////////////////////////////////////////////////////////////////
	
		user = userService.getUser(user.getUserId());
		
		SecurityContextHolder.clearContext();
		
		LoginUser loginUser = new LoginUser(user);
		System.out.println("  #updateUserDetails : "+loginUser);
		
		Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
				loginUser, null, loginUser.getAuthorities());
		System.out.println("  #newAuthentication : "+newAuthentication);
		
		SecurityContextHolder.getContext().setAuthentication(newAuthentication);
		
		System.out.println("   ????????? ???");
		////////////////////////////////////////////////////////////////////////////////
		
		
		return "redirect:/user/updateUser?userId="+user.getUserId();
	}
	
	@GetMapping("deleteUser")		//??????, ?????????
	public String deleteUser()throws Exception {
		
		System.out.println("/user/deleteUser : GET");
		
		return "user/deleteUserView";
	}
	
	@PostMapping("deleteUser")		//??????, ?????????
	public String deleteUser(@RequestParam("password") String password, HttpSession session,
								HttpServletRequest request, HttpServletResponse response)throws Exception {
		
		System.out.println("/user/deleteUser : POST");

		LoginUser sessionUser= (LoginUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = userService.getUser(sessionUser.getUser().getUserId());
		
		if(passwordEncoder.matches(password, user.getPassword())) {
			userService.applyLeave(user);
			System.out.println("???????????? ??????");
		}else {
			System.out.println("???????????? ??????");
		}
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());


		return "redirect:/";
	}
	
	@Scheduled(cron = "0 0 0 * * ?")
	public void leaveUserAuto() throws Exception {
		
		System.out.println("### leaveUserAuto START ###");
		
		Search search = new Search();
		
		Map<String , Object> map=userService.getUserList(search);
		List<User> list = (List<User>) map.get("list");
		
		LocalDate todaysDate = LocalDate.now();

		for(int i = 0; i<list.size(); i++) {
			User user = list.get(i);
			try {
				if(user.getLeaveDate().toString().substring(0,10).equals(todaysDate.toString())) {
					userService.leaveUser(user);;
					System.out.println(user.getUserId()+" is convert to leave");
				}	
			}catch(Exception e){
			}
		}
		System.out.println("### leaveUserAuto END ###");

	}
		
}