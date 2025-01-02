package com.nnk.springboot.integration;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.dto.CurveDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.CurvePointRepository;
import com.nnk.springboot.service.CurveService;
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
public class CurveServiceIT {

    @Autowired
    private CurveService curveService;

    @Autowired
    private CurvePointRepository curvePointRepository;

    @BeforeEach
    void setUp() {
        curvePointRepository.deleteAll();
    }

    @Test
    void saveCurvePoint_ShouldSaveCurveSuccessfully() throws EntitySaveException {
        // Arrange
        CurveDTO curveDTO = new CurveDTO();
        curveDTO.setCurveId(1);
        curveDTO.setTerm(10.5);
        curveDTO.setValue(100.0);

        // Act
        CurvePoint savedCurve = curveService.saveCurvePoint(curveDTO);

        // Assert
        assertNotNull(savedCurve.getId());
        assertEquals(1, savedCurve.getCurveId());
        assertEquals(10.5, savedCurve.getTerm());
        assertEquals(100.0, savedCurve.getValue());
    }

    @Test
    void saveCurvePoint_ShouldThrowExceptionWhenDataInvalid() {
        // Arrange
        CurveDTO curveDTO = new CurveDTO(); // Missing required fields

        // Act & Assert
        assertThrows(EntitySaveException.class, () -> curveService.saveCurvePoint(curveDTO));
    }

    @Test
    void getAllCurvePoint_ShouldReturnAllSavedCurves() throws EntitySaveException {
        // Arrange
        CurveDTO curve1 = new CurveDTO();
        curve1.setCurveId(1);
        curve1.setTerm(10.0);
        curve1.setValue(50.0);
        curveService.saveCurvePoint(curve1);

        CurveDTO curve2 = new CurveDTO();
        curve2.setCurveId(2);
        curve2.setTerm(20.0);
        curve2.setValue(150.0);
        curveService.saveCurvePoint(curve2);

        // Act
        List<CurvePoint> allCurves = curveService.getAllCurvePoint();

        // Assert
        assertEquals(2, allCurves.size());
    }

    @Test
    void getCurveDTOById_ShouldReturnCurveDTO() throws EntityNotFoundException, EntitySaveException {
        // Arrange
        CurveDTO curveDTO = new CurveDTO();
        curveDTO.setCurveId(1);
        curveDTO.setTerm(10.5);
        curveDTO.setValue(100.0);
        CurvePoint savedCurve = curveService.saveCurvePoint(curveDTO);

        int id = Math.toIntExact(savedCurve.getId());

        // Act
        CurveDTO fetchedCurve = curveService.getCurveDTOById(id);

        // Assert
        assertNotNull(fetchedCurve);
        assertEquals(savedCurve.getId(), fetchedCurve.getId());
    }

    @Test
    void getCurveDTOById_ShouldThrowExceptionWhenCurveNotFound() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> curveService.getCurveDTOById(999));
    }

    @Test
    void updateCurvePoint_ShouldUpdateCurveSuccessfully() throws EntitySaveException, EntityNotFoundException {
        // Arrange
        CurveDTO curveDTO = new CurveDTO();
        curveDTO.setCurveId(1);
        curveDTO.setTerm(10.0);
        curveDTO.setValue(50.0);
        CurvePoint savedCurve = curveService.saveCurvePoint(curveDTO);

        CurveDTO updatedCurveDTO = new CurveDTO();
        updatedCurveDTO.setCurveId(1);
        updatedCurveDTO.setTerm(15.0);
        updatedCurveDTO.setValue(200.0);

        int id = Math.toIntExact(savedCurve.getId());
        // Act
        CurvePoint updatedCurve = curveService.updateCurvePoint(id, updatedCurveDTO);

        // Assert
        assertEquals(15.0, updatedCurve.getTerm());
        assertEquals(200.0, updatedCurve.getValue());
    }

    @Test
    void updateCurvePoint_ShouldThrowExceptionWhenCurveNotFound() {
        // Arrange
        CurveDTO updatedCurveDTO = new CurveDTO();
        updatedCurveDTO.setCurveId(1);
        updatedCurveDTO.setTerm(15.0);
        updatedCurveDTO.setValue(200.0);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> curveService.updateCurvePoint(999, updatedCurveDTO));
    }

    @Test
    void deleteCurvePoint_ShouldDeleteCurveSuccessfully() throws EntitySaveException {
        // Arrange
        CurveDTO curveDTO = new CurveDTO();
        curveDTO.setCurveId(1);
        curveDTO.setTerm(10.0);
        curveDTO.setValue(50.0);
        CurvePoint savedCurve = curveService.saveCurvePoint(curveDTO);
        int id = Math.toIntExact(savedCurve.getId());
        // Act
        curveService.deleteCurvePoint(id);
        boolean exists = curvePointRepository.existsById(id);

        // Assert
        assertFalse(exists);
    }

    @Test
    void deleteCurvePoint_ShouldThrowExceptionWhenCurveNotFound() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> curveService.deleteCurvePoint(999));
    }
}
