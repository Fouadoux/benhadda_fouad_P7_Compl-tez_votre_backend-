package com.nnk.springboot.service;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.dto.BidDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.BidListRepository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class BidListServiceTest {

    @Mock
    private BidListRepository bidListRepository;

    @InjectMocks
    private BidListService bidListService;


    @Test
    void testGetAllBidList() {
       //Arrange
        BidList b1 = new BidList();
        b1.setAccount("Account");
        b1.setType("Type");
        b1.setBidQuantity(100D);

        BidList b2 = new BidList();
        b2.setAccount("Account");
        b2.setType("Type");
        b2.setBidQuantity(100D);

        when(bidListRepository.findAll()).thenReturn(List.of(b1,b2));
        //ACT

        List<BidList> bidLists= bidListService.getAllBidList();

        //Assert
        assertFalse(bidListService.getAllBidList().isEmpty());
    }

    @Test
    void testConvertToDto() {
        BidList b1 = new BidList();
        b1.setAccount("Account");
        b1.setType("Type");
        b1.setBidQuantity(100D);

        BidDTO bidDTO = bidListService.convertToDTO(b1);

        assertNotNull(bidDTO);
        assertEquals(bidDTO.getAccount(), b1.getAccount());
        assertEquals(bidDTO.getType(), b1.getType());
        assertEquals(bidDTO.getBidQuantity(), b1.getBidQuantity());
    }

    @Test
    void testConvertToDTOList(){
        BidList b1 = new BidList();
        b1.setAccount("Account");
        b1.setType("Type");
        b1.setBidQuantity(100D);

        BidList b2 = new BidList();
        b2.setAccount("Account");
        b2.setType("Type");
        b2.setBidQuantity(100D);

        List<BidDTO> bidDTOs = bidListService.convertToDTOList(List.of(b1,b2));

        assertNotNull(bidDTOs);
        assertEquals(bidDTOs.get(0).getAccount(), b1.getAccount());
        assertEquals(bidDTOs.get(0).getType(), b1.getType());
        assertEquals(bidDTOs.get(0).getBidQuantity(), b1.getBidQuantity());
        assertEquals(bidDTOs.get(1).getAccount(), b2.getAccount());
        assertEquals(bidDTOs.get(1).getType(), b2.getType());
        assertEquals(bidDTOs.get(1).getBidQuantity(), b2.getBidQuantity());

    }

    @Test
    void testSaveBid_Successfully() {

        BidDTO bidDTO = new BidDTO();
        bidDTO.setAccount("TestAccount");
        bidDTO.setType("TestType");
        bidDTO.setBidQuantity(100D);

        BidList savedMock = new BidList();
        savedMock.setAccount("AccountTest");
        savedMock.setType("TypeTest");
        savedMock.setBidQuantity(100.0);

        when(bidListRepository.save(any(BidList.class))).thenReturn(savedMock);

        BidList result = bidListService.saveBidList(bidDTO);

        assertNotNull(result);
        assertEquals("AccountTest", result.getAccount());
        assertEquals("TypeTest", result.getType());
        verify(bidListRepository, times(1)).save(any(BidList.class));

    }

    @Test
    void testSaveBid_ThrowsEntitySaveException() {
        // GIVEN
        BidDTO bidDTO = new BidDTO();
        bidDTO.setAccount("FailAccount");
        bidDTO.setType("FailType");
        bidDTO.setBidQuantity(999.0);

        doThrow(new DataAccessException("DB error") {}).when(bidListRepository).save(any(BidList.class));

        // WHEN + THEN
        EntitySaveException ex = assertThrows(EntitySaveException.class, () -> {
            bidListService.saveBidList(bidDTO);
        });

        assertEquals("Failed to create bid.", ex.getMessage());
        verify(bidListRepository, times(1)).save(any(BidList.class));
    }

    @Test
    void testUpdateBidList_Success() {
        // GIVEN
        int bidId = 1;
        BidDTO dto = new BidDTO();
        dto.setAccount("NewAccount");
        dto.setType("NewType");
        dto.setBidQuantity(123.45);

        byte bidByte = 1;
        BidList existingBid = new BidList();
        existingBid.setId(bidByte);
        existingBid.setAccount("OldAccount");
        existingBid.setType("OldType");
        existingBid.setBidQuantity(99.99);

        when(bidListRepository.findById(bidId)).thenReturn(Optional.of(existingBid));
        when(bidListRepository.save(any(BidList.class))).thenReturn(existingBid);

        // WHEN
        BidList result = bidListService.updateBidList(bidId, dto);

        // THEN
        assertNotNull(result);
        assertEquals(bidByte, result.getId());
        assertEquals("NewAccount", result.getAccount());
        assertEquals("NewType", result.getType());
        assertEquals(123.45, result.getBidQuantity());

        verify(bidListRepository).findById(bidId);
        verify(bidListRepository).save(existingBid);
    }

    @Test
    void testUpdateBidList_NotFound() {
        // GIVEN
        int bidId = 999;
        BidDTO dto = new BidDTO();
        // ...

        when(bidListRepository.findById(bidId)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(EntityNotFoundException.class, () -> {
            bidListService.updateBidList(bidId, dto);
        });

        verify(bidListRepository).findById(bidId);
        verify(bidListRepository, never()).save(any());
    }

    @Test
    void testUpdateBidList_DataAccessException() {
        // GIVEN
        int bidId = 1;
        BidDTO dto = new BidDTO();
        dto.setAccount("FailAccount");
        dto.setType("FailType");
        dto.setBidQuantity(999.99);

        byte bidByte = 1;
        BidList existingBid = new BidList();
        existingBid.setId(bidByte);

        when(bidListRepository.findById(bidId)).thenReturn(Optional.of(existingBid));
        doThrow(new DataAccessException("DB error") {}).when(bidListRepository).save(existingBid);

        // WHEN + THEN
        EntitySaveException ex = assertThrows(EntitySaveException.class, () -> {
            bidListService.updateBidList(bidId, dto);
        });
        assertTrue(ex.getMessage().contains("Failed to update bid with ID"));

        verify(bidListRepository).findById(bidId);
        verify(bidListRepository).save(existingBid);
    }

    @Test
    void testDeleteBidList_Successfully() {
        // GIVEN
        int bidId = 1;

        // Simuler l'existence de l'entité
        when(bidListRepository.existsById(bidId)).thenReturn(true);

        // WHEN
        bidListService.deleteBidList(bidId);

        // THEN
        verify(bidListRepository).existsById(bidId);
        verify(bidListRepository).deleteById(bidId);
    }

    @Test
    void testDeleteBidList_NotFound() {
        // GIVEN
        int bidId = 999;

        // Simuler l'absence de l'entité
        when(bidListRepository.existsById(bidId)).thenReturn(false);

        // WHEN + THEN
        assertThrows(EntityNotFoundException.class, () -> {
            bidListService.deleteBidList(bidId);
        });

        // Vérifier que deleteById n'a pas été appelé
        verify(bidListRepository, never()).deleteById(anyInt());
    }

    @Test
    void testDeleteBidList_DataAccessException() {
        // GIVEN
        int bidId = 2;
        when(bidListRepository.existsById(bidId)).thenReturn(true);

        // Simuler une exception au moment de la suppression
        doThrow(new DataAccessException("DB error") {})
                .when(bidListRepository).deleteById(bidId);

        // WHEN + THEN
        assertThrows(EntityDeleteException.class, () -> {
            bidListService.deleteBidList(bidId);
        });

        verify(bidListRepository).deleteById(bidId);
    }

    @Test
    void testDeleteBidList_InvalidId() {
        // GIVEN
        int invalidId = 0;

        // WHEN + THEN
        assertThrows(IllegalArgumentException.class, () -> {
            bidListService.deleteBidList(invalidId);
        });

        // Vérifier que le repository n'est jamais appelé
        verify(bidListRepository, never()).existsById(anyInt());
        verify(bidListRepository, never()).deleteById(anyInt());
    }

    @Test
    void testGetBidDTOById_Success() {
        // GIVEN
        byte validId = 1;
        BidList mockBidList = new BidList();
        mockBidList.setId(validId);
        mockBidList.setAccount("TestAccount");
        mockBidList.setType("TestType");
        mockBidList.setBidQuantity(123.45);

        // Simulation : l'entité est trouvée
        when(bidListRepository.findById(1))
                .thenReturn(Optional.of(mockBidList));

        // WHEN
        BidDTO result = bidListService.getBidDTOById(validId);

        // THEN
        assertNotNull(result);
        assertEquals(validId, result.getId());
        assertEquals("TestAccount", result.getAccount());
        assertEquals("TestType", result.getType());
        assertEquals(123.45, result.getBidQuantity());
        verify(bidListRepository).findById(1);
    }

    @Test
    void testGetBidDTOById_NotFound() {
        // GIVEN
        int nonExistentId = 999;
        when(bidListRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(EntityNotFoundException.class, () -> {
            bidListService.getBidDTOById(nonExistentId);
        });
        verify(bidListRepository).findById(nonExistentId);
    }

    @Test
    void testGetBidDTOById_InvalidId() {
        // GIVEN
        int invalidId = 0; // ou -1

        // WHEN + THEN
        assertThrows(IllegalArgumentException.class, () -> {
            bidListService.getBidDTOById(invalidId);
        });

        // Vérifie que le repository n'est jamais appelé
        verify(bidListRepository, never()).findById(anyInt());
    }

}