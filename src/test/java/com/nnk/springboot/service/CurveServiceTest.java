package com.nnk.springboot.service;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.dto.CurveDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.CurvePointRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class CurveServiceTest {

    @Mock
    private CurvePointRepository curvePointRepository;

    @InjectMocks
    private CurveService curveService;


    @Test
    void testGetAllCurvePoints() {
        //Arrange
        CurvePoint curve = new CurvePoint();
        curve.setCurveId(1);
        curve.setValue(11D);

        CurvePoint curve2 = new CurvePoint();
        curve2.setCurveId(2);
        curve2.setValue(22D);

        when(curvePointRepository.findAll()).thenReturn(List.of(curve, curve2));

        List<CurvePoint> curvePointList = curveService.getAllCurvePoint();
        assertNotNull(curvePointList);
        assertEquals(2, curvePointList.size());
        assertEquals(1, curvePointList.get(0).getCurveId());
        assertEquals(2, curvePointList.get(1).getCurveId());
    }

    @Test
    void testConvertToDTO(){
        CurvePoint curvePoint = new CurvePoint();
        curvePoint.setCurveId(1);
        curvePoint.setId(1L);
        curvePoint.setValue(11D);
        curvePoint.setTerm(10D);


        CurveDTO curveDTO = curveService.convertToDTO(curvePoint);

        assertNotNull(curveDTO);
        assertEquals(11D, curveDTO.getValue());
        assertEquals(10L, curveDTO.getTerm());
        assertEquals(1, curveDTO.getCurveId());

    }

    @Test
    void testConvertToDTOList(){
        CurvePoint curve1 = new CurvePoint();
        curve1.setCurveId(1);
        curve1.setId(1L);
        curve1.setValue(11D);
        curve1.setTerm(10D);

        CurvePoint curve2 = new CurvePoint();
        curve2.setCurveId(2);
        curve2.setId(2L);
        curve2.setValue(22D);
        curve2.setTerm(10D);

        List<CurveDTO> curveDTOList = curveService.convertToDtoList(List.of(curve1, curve2));
        assertNotNull(curveDTOList);
        assertEquals(2, curveDTOList.size());
        assertEquals(1, curveDTOList.get(0).getCurveId());
        assertEquals(2, curveDTOList.get(1).getCurveId());
    }

    @Test
    void testSaveCurvePoint_Successfully(){

        CurveDTO curveDTO = new CurveDTO();
        curveDTO.setId(1);
        curveDTO.setCurveId(1);
        curveDTO.setValue(11D);
        curveDTO.setTerm(10D);

        CurvePoint saveMock = new CurvePoint();
        curveDTO.setId(1);
        saveMock.setCurveId(1);
        saveMock.setValue(11D);
        saveMock.setTerm(10D);
        when(curvePointRepository.save(any(CurvePoint.class))).thenReturn(saveMock);

        CurvePoint result = curveService.saveCurvePoint(curveDTO);
        assertNotNull(result);
        assertEquals(11D, result.getValue());
        assertEquals(10D, curveDTO.getTerm());
        assertEquals(1, curveDTO.getCurveId());


    }

    @Test
    void testSaveCurvePoint_Exception(){
        CurveDTO curveDTO = new CurveDTO();
        curveDTO.setId(1);
        curveDTO.setCurveId(1);
        curveDTO.setValue(11D);
        curveDTO.setTerm(10D);

        doThrow(new DataAccessException("DB error"){}).when(curvePointRepository).save(any(CurvePoint.class));

        EntitySaveException ex = assertThrows(EntitySaveException.class, () -> curveService.saveCurvePoint(curveDTO));

        assertEquals("Failed to add curve point with ID 1", ex.getMessage());
        verify(curvePointRepository, times(1)).save(any(CurvePoint.class));

    }

    @Test
    void testUpdateCurvePoint_Successfully(){
        CurveDTO curveDTO = new CurveDTO();
        curveDTO.setId(1);
        curveDTO.setCurveId(1);
        curveDTO.setValue(11D);
        curveDTO.setTerm(10D);
        CurvePoint curvePoint = new CurvePoint();
        curvePoint.setCurveId(1);
        curvePoint.setId(1L);
        curvePoint.setValue(11D);
        curvePoint.setTerm(10D);

        when(curvePointRepository.findById(1)).thenReturn(Optional.of(curvePoint));

        when(curvePointRepository.save(curvePoint)).thenAnswer(invocation -> invocation.getArgument(0, CurvePoint.class));

        CurvePoint result = curveService.updateCurvePoint(1, curveDTO);

        //THEN

        assertNotNull(result);
        assertEquals(11D, result.getValue());
        assertEquals(10D, curveDTO.getTerm());
        assertEquals(1, curveDTO.getCurveId());

        verify(curvePointRepository).findById(1);
        verify(curvePointRepository).save(curvePoint);

    }

    @Test
    void testUpdateCurvePoint_NotFound(){
        int id=100;
        CurveDTO curveDTO = new CurveDTO();
        //...

        when(curvePointRepository.findById(id)).thenReturn(Optional.empty());

        //When + Then
        assertThrows(EntityNotFoundException.class, () -> curveService.updateCurvePoint(id, curveDTO));

        verify(curvePointRepository).findById(id);
        verify(curvePointRepository,never()).save(any());

    }

    @Test
    void testUpdateCurvePoint_DataAccessException(){

        CurveDTO curveDTO = new CurveDTO();
        curveDTO.setId(1);
        curveDTO.setCurveId(1);
        curveDTO.setValue(11D);
        curveDTO.setTerm(10D);

        CurvePoint curvePoint = new CurvePoint();
        curvePoint.setId(1L);

        when(curvePointRepository.findById(1)).thenReturn(Optional.of(curvePoint));
        doThrow(new DataAccessException("DB error") {}).when(curvePointRepository).save(any(CurvePoint.class));

        EntitySaveException ex = assertThrows(EntitySaveException.class, () -> curveService.updateCurvePoint(1, curveDTO));

        assertEquals("Failed to update curve point with ID 1", ex.getMessage());
        verify(curvePointRepository).findById(1);
        verify(curvePointRepository).save(curvePoint);

    }

    @Test
    void testDeleteCurvePoint_Successfully(){
        when(curvePointRepository.existsById(1)).thenReturn(true);

        curveService.deleteCurvePoint(1);

        verify(curvePointRepository).deleteById(1);
        verify(curvePointRepository).existsById(1);

    }

    @Test
    void testDeleteCurvePoint_NotFound(){
        int id = 999;
        when(curvePointRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> curveService.deleteCurvePoint(id));

        verify(curvePointRepository,never()).deleteById(id);
    }

    @Test
    void testDeleteCurvePoint_DataAccessException(){
        int id = 999;
        when(curvePointRepository.existsById(id)).thenReturn(true);
        doThrow(new DataAccessException("DB error") {}).when(curvePointRepository).deleteById(id);

        assertThrows(EntityDeleteException.class, () -> curveService.deleteCurvePoint(id));

        verify(curvePointRepository).deleteById(id);
    }

    @Test
    void testDeleteCurvePoint_InvalidId(){
        int id = 0;

        assertThrows(IllegalArgumentException.class, () -> curveService.deleteCurvePoint(id));

        verify(curvePointRepository,never()).deleteById(id);
        verify(curvePointRepository,never()).existsById(id);
    }

    @Test
    void testGetCurveDTOById_Successfully(){
        CurvePoint curve = new CurvePoint();
        curve.setId(1L);
        curve.setCurveId(1);
        curve.setValue(11D);
        curve.setTerm(10D);

        when(curvePointRepository.findById(1)).thenReturn(Optional.of(curve));
        CurveDTO result = curveService.getCurveDTOById(1);
        assertNotNull(result);
        assertEquals(11D, result.getValue());

    }

    @Test
    void testGetCurveDTOById_NotFound(){
        int nonExistentId = 999;
        when(curvePointRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(EntityNotFoundException.class, () -> {
            curveService.getCurveDTOById(nonExistentId);
        });
        verify(curvePointRepository).findById(nonExistentId);
    }

    @Test
    void testGetCurveDTOById_InvalidId(){

        int invalidId = 0; // ou -1

        // WHEN + THEN
        assertThrows(IllegalArgumentException.class, () -> {
            curveService.getCurveDTOById(invalidId);
        });

        // Vérifie que le repository n'est jamais appelé
        verify(curvePointRepository, never()).findById(anyInt());

    }
}