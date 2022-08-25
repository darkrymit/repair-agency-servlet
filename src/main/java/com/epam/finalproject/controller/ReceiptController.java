package com.epam.finalproject.controller;

import com.epam.finalproject.dto.*;
import com.epam.finalproject.framework.security.UserDetails;
import com.epam.finalproject.framework.web.annotation.*;
import com.epam.finalproject.model.entity.AppCurrency;
import com.epam.finalproject.request.ReceiptResponseCreateRequest;
import com.epam.finalproject.request.receipt.create.ReceiptCreateRequest;
import com.epam.finalproject.request.receipt.pay.ReceiptPayRequest;
import com.epam.finalproject.request.receipt.update.ReceiptUpdateRequest;
import com.epam.finalproject.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;

import java.util.List;

@Controller
@RequestMapping("/order")
public class ReceiptController {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ReceiptController.class);
    ReceiptService receiptService;

    ReceiptStatusFlowService receiptStatusFlowService;

    ReceiptResponseService receiptResponseService;

    RepairCategoryService repairCategoryService;

    RepairWorkService repairWorkService;

    WalletService walletService;
    AppCurrencyService appCurrencyService;

    public ReceiptController(ReceiptService receiptService, ReceiptStatusFlowService receiptStatusFlowService,
            ReceiptResponseService receiptResponseService, RepairCategoryService repairCategoryService,
            RepairWorkService repairWorkService, WalletService walletService, AppCurrencyService appCurrencyService) {
        this.receiptService = receiptService;
        this.receiptStatusFlowService = receiptStatusFlowService;
        this.receiptResponseService = receiptResponseService;
        this.repairCategoryService = repairCategoryService;
        this.repairWorkService = repairWorkService;
        this.walletService = walletService;
        this.appCurrencyService = appCurrencyService;
    }

    @GetMapping("/{id}/update")
    String updatePage(HttpServletRequest request, UserDetails userDetails, @PathVariable("id") Long id) {
        ReceiptDTO receipt = receiptService.findById(id);
        List<ReceiptStatusFlowDTO> flows = receiptStatusFlowService.listAllAvailableForUser(receipt.getStatus().getId(),userDetails.getUsername());
        List<AppCurrency> currencies = appCurrencyService.findAll();
        request.setAttribute("order",receipt);
        request.setAttribute("flows",flows);
        request.setAttribute("currencies",currencies);
        return "orderUpdate";
    }
    @PostMapping("/{id}/update")
    String update(@RequestJsonObject ReceiptUpdateRequest updateRequest, @PathVariable("id") Long id) {
        updateRequest.setId(id);
        ReceiptDTO receipt = receiptService.update(updateRequest);
        return "redirect:/order/"+receipt.getId();
    }

    @PostMapping("/{id}/response/create")
    String responseCreate( ReceiptResponseCreateRequest createRequest, UserDetails userDetails, @PathVariable("id") Long id) {
        createRequest.setReceiptId(id);
        log.info(createRequest.toString());
        receiptResponseService.createNew(createRequest,userDetails.getUsername());
        return "redirect:/order/"+id;
    }

    @PostMapping("/{id}/status/change")
    String updateStatus(UserDetails userDetails,@PathVariable("id") Long id,@RequestParam("statusId") Long statusId) {
        ReceiptDTO receipt = receiptService.updateStatus(id,statusId,userDetails.getUsername());
        return "redirect:/order/"+receipt.getId();
    }

    @GetMapping(value ="/{id}/pay")
    String pay(HttpServletRequest request,  UserDetails userDetails, @PathVariable("id") Long id) {
        ReceiptDTO receipt = receiptService.findById(id);
        List<WalletDTO> wallets = walletService.findAllByUsername(userDetails.getUsername());
       request.setAttribute("order",receipt);
       request.setAttribute("wallets",wallets);
        return "orderPay";
    }

    @PostMapping("/{id}/pay")
    String pay(UserDetails userDetails,@RequestObject ReceiptPayRequest payRequest, @PathVariable("id") Long id) {
        payRequest.setId(id);
        ReceiptDTO receipt = receiptService.pay(payRequest,userDetails.getUsername());
        return "redirect:/order/"+receipt.getId();
    }

    @GetMapping("/{id}")
    String show(HttpServletRequest request, @PathVariable("id") Long id) {
        ReceiptDTO receipt = receiptService.findById(id);
        if (receiptResponseService.existByReceiptId(id)){
            ReceiptResponseDTO response = receiptResponseService.findByReceiptId(id);
            request.setAttribute("response",response);
        }
        log.trace("Work {}",receipt);
        request.setAttribute("order",receipt);
        return "order";
    }

    @PostMapping("/create")
    String create(UserDetails userDetails, @RequestJsonObject ReceiptCreateRequest createRequest) {
        ReceiptDTO receipt = receiptService.createNew(createRequest,userDetails.getUsername());
        return "redirect:/order/"+receipt.getId();
    }
    @GetMapping("/create")
    String create(HttpServletRequest request, @RequestParam("category") String category, @RequestParam("currency")  String currency) {
        RepairCategoryDTO repairCategory = repairCategoryService.findByKeyName(category);
        List<RepairWorkDTO> repairWorks = repairWorkService.findByCategoryKey(category);
        AppCurrency appCurrency = appCurrencyService.findByCode(currency);
        request.setAttribute("works",repairWorks);
        request.setAttribute("category",repairCategory);
        request.setAttribute("currency",appCurrency);
        return "orderCreate";
    }


}