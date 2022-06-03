package com.mvc.forrest.controller.storage;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mvc.forrest.service.domain.Page;
import com.mvc.forrest.service.domain.Product;
import com.mvc.forrest.service.domain.Search;
import com.mvc.forrest.service.domain.Storage;
import com.mvc.forrest.service.domain.User;
import com.mvc.forrest.service.product.ProductService;
import com.mvc.forrest.service.storage.StorageService;
import com.mvc.forrest.service.user.UserService;





@Controller
@RequestMapping("/storage/*")
public class StorageController {
	
	@Autowired
	public StorageService storageService ;
	
	@Autowired
	public ProductService productService ;
	
	@Autowired
	public UserService userService;
	
	public StorageController() {
		System.out.println(this.getClass());
	}

	@Value("5")
	int pageUnit;
	
	@Value("10")
	int pageSize;
		
	
	//보관 메인화면 단순 네비게이션
	@GetMapping("storageMain")
	public String storageMain() throws Exception  {
		
		return "storage/storageMain";
	}
	
	//보관물품등록을 위한 페이지로 네비게이션
	@GetMapping("addStorage")
	public String addStorageGet(Model model, HttpSession session) throws Exception {
		
		String userId = ((User) session.getAttribute("user")).getUserId();
		model.addAttribute("user", userService.getUser(userId));
		
		return "storage/addStorage";
	}
	
	//결제정보를 리턴받고 물품과 보관테이블에 동시에 추가
	@PostMapping("addStorage")
	public String addStoragePost(@RequestParam("imp_uid") int imp_uid,
												@RequestParam("merchant_uid") String merchant_uid,
												@ModelAttribute("product") Product product,
												@ModelAttribute("storage") Storage storage,
												HttpSession session, Model model) throws Exception {

		product.setUserId(((User) session.getAttribute("user")).getUserId());
		productService.addProduct(product);
		
		storage.setPaymentNo(merchant_uid);
		storage.setTranNo(imp_uid);
		
		storageService.addStorage(storage);
		
		model.addAttribute("storage", storage);
		
		return "storage/getStorage";
	}
	
	
	@RequestMapping("listStorage")
	public String listStorage(@ModelAttribute("search") Search search, HttpSession httpSession, Model model) throws Exception {
		
		if (search.getCurrentPage() == 0) {
			search.setCurrentPage(1);
		}
		
		search.setPageSize(pageSize);
		
		String userId = ((User)httpSession.getAttribute("user")).getUserId();
		
		Map<String, Object> map = new HashMap<>();
		map.put("search", search);
		map.put("userId", userId);
		
		Map<String, Object> mapStorage = storageService.getStorageList(map);
		
		Page resultPage = new Page(search.getCurrentPage(), ((Integer)mapStorage.get("totalCount")).intValue(), pageUnit, pageSize );
		
		model.addAttribute("list", mapStorage.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		
		return "storage/listStorage";
	}
	
	@RequestMapping("listStorageForAdmin")
	public String listStorageForAdmin(@ModelAttribute("search") Search search, Model model) throws Exception {
		
		if (search.getCurrentPage() == 0) {
			search.setCurrentPage(1);
		}
		
		search.setPageSize(pageSize);
		
		Map<String, Object> map = storageService.getStorageListForAdmin(search);
		Page resultPage = new Page(search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize );
		
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		
		return "storage/listStorageForAdmin";
	}
		
	@GetMapping("extendStorage")
	public String extendStorageGet() {
		
		return null;
	}
	
	@PostMapping("extendStorage")
	public String extendStoragePost() {
		
		return null;
	}
	
	@GetMapping("getStorage")
	public String getStorage(@RequestParam("tranNo") int tranNo, Model model) throws Exception {
		
		model.addAttribute("storage", storageService.getStorage(tranNo));
	
		return "storage/getStorage";
	}

}