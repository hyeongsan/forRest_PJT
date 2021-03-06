package com.mvc.forrest.controller.product;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mvc.forrest.common.utils.FileNameUtils;
import com.mvc.forrest.common.utils.FileUtils;
import com.mvc.forrest.config.auth.LoginUser;
import com.mvc.forrest.service.domain.Img;
import com.mvc.forrest.service.domain.Page;
import com.mvc.forrest.service.domain.Product;
import com.mvc.forrest.service.domain.Rental;
import com.mvc.forrest.service.domain.Search;
import com.mvc.forrest.service.domain.Storage;
import com.mvc.forrest.service.domain.User;
import com.mvc.forrest.service.firebase.FCMService;
import com.mvc.forrest.service.product.ProductService;
import com.mvc.forrest.service.rental.RentalService;
import com.mvc.forrest.service.rentalreview.RentalReviewService;
import com.mvc.forrest.service.storage.StorageService;
import com.mvc.forrest.service.user.UserService;





@Controller
@RequestMapping("/product/*")
public class ProductController {
	
	@Autowired
	public ProductService productService;
	
	@Autowired
	public StorageService storageService;
	
	@Autowired
	public RentalService rentalService;
	
	@Autowired
	public UserService userService;
	
	@Autowired
	public RentalReviewService rentalReviewService;
	
	@Autowired
	public FileUtils fileUtils;
	
	@Autowired
	public FCMService fcmService;
	
	public ProductController() {
		System.out.println(this.getClass());
	}
	
	@Value("5")
	int pageUnit;
	
	@Value("8")
	int pageSize;
	
	@GetMapping("updateRecentImg")
	public String updateRecentImgGet(@RequestParam("prodNo") String prodNo, Model model) throws Exception {
			
		model.addAttribute("prodNo", prodNo);
		
		return "product/updateRecentImg";
	}

	

	@PostMapping("updateRecentImg")
	public String updateRecentImgPost(@RequestParam("fileName") MultipartFile fileName, @ModelAttribute("product") Product product) throws Exception {
	
		String temDir = "C:\\\\Users\\\\bitcamp\\\\git\\\\forRest\\\\Bit-forRest\\\\src\\\\main\\\\resources\\\\static\\\\images\\\\uploadFiles";
		String convertFileName = FileNameUtils.fileNameConvert(fileName.getOriginalFilename());
		
		
		product.setProdNo(product.getProdNo());
		product.setRecentImg(convertFileName);
		
		if(!fileName.getOriginalFilename().isEmpty()) {
			
			fileName.transferTo(new File(temDir, convertFileName));
			System.out.println("????????? :: "+convertFileName);
			
			product.setRecentImg(convertFileName);			
		}else {
			System.out.println("??????????????? ??????...?");
		}
		
		productService.updateRecentImg(product);
		
		return null;
	}
	
	//??????, ????????? ??????
	@GetMapping("updateProduct")
	public String updateProductGet(@RequestParam("prodNo") String prodNo, Model model) throws Exception {
		
		Product product = productService.getProduct(prodNo);
		
		model.addAttribute("product", product);	
	
		return "product/updateProduct";
	}
	

	@PostMapping("updateProduct")
	public String updateProductPost(@ModelAttribute("product") Product product, @RequestParam("uploadFile") List<MultipartFile> uploadFile) throws Exception {
		
		if(uploadFile.size()!=1) {
			fileUtils.deleteImg(product.getProdNo());
			String mainImg=fileUtils.uploadFiles(uploadFile, product.getProdNo(), "product");
			product.setProdImg(mainImg);
		}
		
		productService.updateProduct(product);
		
		return "redirect:/storage/listStorage";
	}
	
	//????????? ??????
	// ???????????? ????????? ???????????? ( ?????? )
	@RequestMapping("updateProductCondition")
	public String updateProductCondition(@RequestParam("prodNo") String prodNo) throws Exception {
		
		Product product = productService.getProduct(prodNo);		
		
		if(product.getProdCondition().equals("???????????????????????????")) {
			product.setProdCondition("?????????");
		} else if (product.getProdCondition().equals("?????????")){
			product.setProdCondition("?????????");
			
		} else if (product.getProdCondition().equals("?????????????????????")){
			product.setProdCondition("????????????");
		//????????????
		} 
		
		productService.updateProductCondition(product);
	
		return "redirect:/storage/listStorageForAdmin";
	}
	

		// ???????????? ????????? ???????????? ( ?????? )
		@RequestMapping("updateRentalProductCondition")
		public String updateRentalProductCondition(@RequestParam("prodNo") String prodNo, @RequestParam("tranNo") String tranNo) throws Exception {
			Product product = productService.getProduct(prodNo);		
            Rental rental = rentalService.getRental(tranNo);
            
            System.out.println("tranNo:"+tranNo);
            
			//????????????
			 if(product.getProdCondition().equals("???????????????????????????")) {
				product.setProdCondition("?????????");
			} else if(product.getProdCondition().equals("?????????")) {
				product.setProdCondition("?????????");
			} else if(product.getProdCondition().equals("?????????")) {
				product.setProdCondition("?????????");
				rental.setComplete(1);
				rentalService.updateComplete(rental);
			} 
			
			productService.updateProductCondition(product);
			
		
			return "redirect:/rental/listRentalForAdmin";
      
		}
	

	//???????????? ??????????????? ????????????
	@RequestMapping("updateProductAllCondition")
	public String updateProductAllCondition(@RequestParam("prodNo") String[] prodNo) throws Exception {
		
		//prodNo??? ?????? productCondition????????? ?????? ??????
		String[] productCondition =  new String[prodNo.length];
		for(int i=0; i<prodNo.length; i++) {
			productCondition[i] = productService.getProduct(prodNo[i]).getProdCondition();
		}
		
		for(int i=0; i<prodNo.length; i++) {
			
			Product product = productService.getProduct(prodNo[i]);
	
			if(productCondition[i].equals("???????????????????????????")) {
				product.setProdCondition("?????????");
			} else if (productCondition[i].equals("?????????")){
				product.setProdCondition("?????????");
			} else if (productCondition[i].equals("?????????????????????")){
				product.setProdCondition("????????????");
			}
			//????????????
	
			productService.updateProductCondition(product);
		}
		
		return "redirect:/storage/listStorageForAdmin";
	}
	
	
		//???????????? ??????????????? ???????????? ( ?????? )
		@RequestMapping("updateRentalProductAllCondition")
		public String updateRentalProductAllCondition(@RequestParam("tranNo") String[] tranNo) throws Exception {
			
			 String[] prodNo = new String[tranNo.length];
			 for(int i=0; i<tranNo.length; i++) {
				 prodNo[i] = rentalService.getRental(tranNo[i]).getProdNo();
				}
			 
			 String[] productCondition =  new String[prodNo.length];
				for(int i=0; i<prodNo.length; i++) {
					productCondition[i] = productService.getProduct(prodNo[i]).getProdCondition();
				}
			
			
			for(int i=0; i<prodNo.length; i++) {
				
				Product product = productService.getProduct(prodNo[i]);
				Rental rental = rentalService.getRental(tranNo[i]);
			
				//????????????
				
				if(productCondition[i].equals("???????????????????????????")) {
					product.setProdCondition("?????????");
				} else if(productCondition[i].equals("?????????")) {
					product.setProdCondition("?????????");
				} else if(product.getProdCondition().equals("?????????")) {
					product.setProdCondition("?????????");
					rental.setComplete(1);
					rentalService.updateComplete(rental);
				} 
			
				productService.updateProductCondition(product);
			}
			
			return "redirect:/rental/listRentalForAdmin";
		}
	
	//????????? ??????????????????????????? ??????????????? ???????????? ????????? ?????????????????? ??????
	@RequestMapping("cancelProduct")
	public String cancelProduct (@RequestParam("prodNo") String prodNo) throws Exception {
		
		Product product = productService.getProduct(prodNo);

		if(product.getProdCondition().equals("???????????????????????????")) {
			product.setProdCondition("????????????");
			
		} else if(product.getProdCondition().equals("?????????")) {
			product.setProdCondition("?????????????????????");
			
		} else if(product.getProdCondition().equals("?????????????????????")) {
			product.setProdCondition("?????????");
		}
		
		productService.updateProductCondition(product);
		
		return "redirect:/storage/listStorage";
	}
	
	//????????? ???????????? ??????????????? ??????
		@RequestMapping("cancelRentalProduct")
		public String cancelRentalProduct (@RequestParam("prodNo") String prodNo,@RequestParam("tranNo") String tranNo) throws Exception {
			
			Product product = productService.getProduct(prodNo);
			
			if(product.getProdCondition().equals("???????????????????????????")) {
				product.setProdCondition("?????????");
			}	
			
			Rental rental = new Rental();
			rental.setTranNo(tranNo);
			rental.setCancelComplete(1);
			
			rentalService.updateCancelDone(rental);
			productService.updateProductCondition(product);
			
			return "redirect:/rental/listRental";
		}
	
	
	//??????, ????????? ??????
	@RequestMapping("getProduct")
	public String getProduct(@RequestParam("prodNo") String prodNo, Model model) throws Exception {

		Product product = productService.getProduct(prodNo);
		
		//???????????? ?????????????????? ?????????
		LoginUser loginUser= (LoginUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId= loginUser.getUser().getUserId();
		
		//getProduct?????? ??????????????? ???????????? ?????? ????????????????????? ?????? ????????????
		User sessionUser = userService.getUser(userId);
		
		//???????????????????????? ?????????????????? ?????????
		Map<String, Object> map = rentalReviewService.getRentalReviewList(prodNo);
		
		List<Img> imglist = fileUtils.getProductImgList(prodNo);
		System.out.println(imglist);
		model.addAttribute("product", product);
		model.addAttribute("sessionUser", sessionUser);
		model.addAttribute("list", map.get("list"));
		model.addAttribute("imglist", imglist);
		
		return "product/getProduct";
	}
	
	//???????????????
	@RequestMapping("listProduct")
	public String listProduct(@ModelAttribute("search") Search search, Model model) throws Exception {
	
	//??????????????? ?????? ??????
		if(pageSize != 8) {
			return "main/index";
		}
		
		//??????????????? ????????? ??????????????? ????????????????????? value??? null??? ??????
		if(search.getSearchCategory()=="") {
			search.setSearchCategory(null);
		}
		
		if(search.getSearchKeyword()=="") {
			search.setSearchKeyword(null);
		}
		
		if(search.getOrderCondition()=="") {
			search.setSearchKeyword(null);
		}
		
		if(search.getCurrentPage()==0) {
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		Map<String, Object> map = productService.getProductList(search);
		List<Product> listName = productService.getProductNames();
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		model.addAttribute("prodNames", listName);
		
		return "product/listProduct";
	}
	
	@RequestMapping("listProductAfterLogin")
	public String listProductAfterLogin(@ModelAttribute("search") Search search, Model model)
			throws Exception {
		
		
		if(search.getSearchCategory()=="") {
			search.setSearchCategory(null);
		}
		
		if(search.getSearchKeyword()=="") {
			search.setSearchKeyword(null);
		}
		
		if(search.getOrderCondition()=="") {
			search.setSearchKeyword(null);
		}
		
		if(search.getCurrentPage()==0) {
			search.setCurrentPage(1);
		}
		
		search.setPageSize(pageSize);

		LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId = loginUser.getUser().getUserId();
		
		
		List<Product> list = productService.getProductListHasUser(search, userId);
		List<Product> listName = productService.getProductNames();
		
		Page resultPage = new Page(search.getCurrentPage(), productService.getTotalCount(search), pageUnit, pageSize);
		
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("loginUserId", userId);
		model.addAttribute("list", list);
		model.addAttribute("search", search);
		model.addAttribute("prodNames", listName);

		return "product/listProduct";
	}
	
	//????????? ????????? ??????????????? ????????? ????????? ????????? ???????????? ??????(09 30)
	@Scheduled(cron = "0 30 09 * * ?")
	public void updateProductConditionAuto() throws Exception {
		
		System.out.println("???????????? ?????????");
		
		List<Storage> list = storageService.getExpiredStorageList();
		
		
		for(Storage storage : list) {
			
			Product product = storage.getStorageProd();
			product.setProdCondition("????????????");
			
			productService.updateProductCondition(product);
			
		}
		
		System.out.println("list:"+ list);
	}

	

}