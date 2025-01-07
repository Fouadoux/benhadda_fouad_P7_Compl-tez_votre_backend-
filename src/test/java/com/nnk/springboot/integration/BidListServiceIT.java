package com.nnk.springboot.integration;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.dto.BidDTO;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.BidListRepository;
import com.nnk.springboot.service.BidListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class BidListServiceIT {

    @Autowired
    private BidListService bidListService;

    @Autowired
    private BidListRepository bidListRepository;

    @BeforeEach
    void setUp() {
        bidListRepository.deleteAll();
    }

    @Test
    void saveBidList_ShouldSaveBidSuccessfully() throws EntitySaveException {
        // Arrange
        BidDTO bidDTO = new BidDTO();
        bidDTO.setAccount("TestAccount");
        bidDTO.setType("TestType");
        bidDTO.setBidQuantity(100D);

        // Act
        BidList savedBid = bidListService.saveBidList(bidDTO);

        // Assert
        assertNotNull(savedBid.getId());
        assertEquals("TestAccount", savedBid.getAccount());
        assertEquals("TestType", savedBid.getType());
        assertEquals(100D, savedBid.getBidQuantity());
    }

    @Test
    void saveBidList_ShouldThrowExceptionWhenDataInvalid() {
        // Arrange
        BidDTO bidDTO = new BidDTO(); // Missing required fields

        // Act & Assert
        assertThrows(EntitySaveException.class, () -> bidListService.saveBidList(bidDTO));
    }


    @Test
    void getAllBidList_ShouldReturnAllSavedBids() throws EntitySaveException {
        // Arrange
        BidDTO bid1 = new BidDTO();
        bid1.setAccount("Account1");
        bid1.setType("Type1");
        bid1.setBidQuantity(50D);
        bidListService.saveBidList(bid1);

        BidDTO bid2 = new BidDTO();
        bid2.setAccount("Account2");
        bid2.setType("Type2");
        bid2.setBidQuantity(50D);
        bidListService.saveBidList(bid2);

        // Act
        List<BidList> allBids = bidListService.getAllBidList();

        // Assert
        assertEquals(2, allBids.size());
    }

    @Test
    void updateBidList_ShouldUpdateBidSuccessfully() throws EntitySaveException {
        // Arrange
        BidDTO bidDTO = new BidDTO();
        bidDTO.setAccount("OldAccount");
        bidDTO.setType("OldType");
        bidDTO.setBidQuantity(10D);
        BidList savedBid = bidListService.saveBidList(bidDTO);

        BidDTO updatedBidDTO = new BidDTO();
        updatedBidDTO.setAccount("NewAccount");
        updatedBidDTO.setType("NewType");
        updatedBidDTO.setBidQuantity(200D);

        // Act
        bidListService.updateBidList(savedBid.getId(), updatedBidDTO);
        BidList updatedBid = bidListRepository.findById(Integer.valueOf(savedBid.getId())).orElse(null);

        // Assert
        assertNotNull(updatedBid);
        assertEquals("NewAccount", updatedBid.getAccount());
        assertEquals("NewType", updatedBid.getType());
        assertEquals(200D, updatedBid.getBidQuantity());
    }

    @Test
    void updateBidList_ShouldThrowExceptionWhenBidNotFound() {
        // Arrange
        int invalidId = 999;
        BidDTO updatedBidDTO = new BidDTO();
        updatedBidDTO.setAccount("NewAccount");
        updatedBidDTO.setType("NewType");
        updatedBidDTO.setBidQuantity(200D);

        // Act & Assert
        assertThrows(DataAccessException.class, () -> bidListService.updateBidList(invalidId, updatedBidDTO));
    }

    @Test
    void deleteBidList_ShouldDeleteBidSuccessfully() throws EntitySaveException {
        // Arrange
        BidDTO bidDTO = new BidDTO();
        bidDTO.setAccount("AccountToDelete");
        bidDTO.setType("TypeToDelete");
        bidDTO.setBidQuantity(100D);
        BidList savedBid = bidListService.saveBidList(bidDTO);

        // Act
        bidListService.deleteBidList(savedBid.getId());
        boolean exists = bidListRepository.existsById(Integer.valueOf(savedBid.getId()));

        // Assert
        assertFalse(exists);
    }

    @Test
    void deleteBidList_ShouldThrowExceptionWhenBidNotFound() {
        // Arrange
        int invalidId = 999;

        // Act & Assert
        assertThrows(DataAccessException.class, () -> bidListService.deleteBidList(invalidId));
    }
}
