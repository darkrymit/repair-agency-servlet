package com.epam.finalproject.controller;

import com.epam.finalproject.dto.ReceiptDTO;
import com.epam.finalproject.dto.ReceiptResponseDTO;
import com.epam.finalproject.dto.ReceiptStatusFlowDTO;
import com.epam.finalproject.dto.WalletDTO;
import com.epam.finalproject.framework.data.Page;
import com.epam.finalproject.framework.data.PageRequest;
import com.epam.finalproject.framework.security.UserDetails;
import com.epam.finalproject.framework.web.annotation.*;
import com.epam.finalproject.request.search.ReceiptWithCustomerSearchRequest;
import com.epam.finalproject.service.ReceiptResponseService;
import com.epam.finalproject.service.ReceiptStatusFlowService;
import com.epam.finalproject.service.SearchService;
import com.epam.finalproject.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    public static final String ACTIVE = "active";

    public static final String MASTER_VIEW = "cabinet";

    public static final String TYPE = "type";
    public static final String CUSTOMER = "customer";
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CustomerController.class);

    SearchService searchService;

    ReceiptResponseService receiptResponseService;

    ReceiptStatusFlowService receiptStatusFlowService;

    WalletService walletService;

    public CustomerController(SearchService searchService, ReceiptResponseService receiptResponseService,
            ReceiptStatusFlowService receiptStatusFlowService, WalletService walletService) {
        this.searchService = searchService;
        this.receiptResponseService = receiptResponseService;
        this.receiptStatusFlowService = receiptStatusFlowService;
        this.walletService = walletService;
    }


    @GetMapping("/orders")
    String ordersPage(HttpServletRequest request, UserDetails userDetails,@RequestObject ReceiptWithCustomerSearchRequest searchRequest) {
        log.trace("Request {}",searchRequest);
        Page<ReceiptDTO> receipts = searchService.findBySearch(searchRequest,userDetails.getUsername());
        List<ReceiptStatusFlowDTO> flows = receiptStatusFlowService.listAllAvailableForUser(userDetails.getUsername());
        request.setAttribute("flows",flows);
        request.setAttribute("search",searchRequest);
        request.setAttribute("receipts", receipts);
        request.setAttribute(ACTIVE, "orders");
        request.setAttribute(TYPE, CUSTOMER);
        return MASTER_VIEW;
    }
    @GetMapping("/wallets")
    String walletsPage(HttpServletRequest request, UserDetails userDetails, @RequestParam(required = false) Integer page) {
        int actualPage = Optional.ofNullable(page).orElse(0);
        Page<WalletDTO> wallets = walletService.findAllByUsername(PageRequest.of(actualPage,5),userDetails.getUsername());
        request.setAttribute("wallets", wallets);
        request.setAttribute(ACTIVE, "wallets");
        request.setAttribute(TYPE, CUSTOMER);
        return MASTER_VIEW;
    }
    @GetMapping("/responses")
    String responsesPage(HttpServletRequest request, UserDetails userDetails,@RequestParam(required = false) Integer page) {
        int actualPage = Optional.ofNullable(page).orElse(0);
        Page<ReceiptResponseDTO> responses = receiptResponseService.findByCustomerUsername(userDetails.getUsername(), PageRequest.of(actualPage,5));
        request.setAttribute("responses", responses);
        request.setAttribute(ACTIVE, "responses");
        request.setAttribute(TYPE, CUSTOMER);
        return MASTER_VIEW;
    }
}