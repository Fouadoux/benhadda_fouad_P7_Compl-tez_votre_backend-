package com.nnk.springboot.service;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.dto.CurveDTO;
import com.nnk.springboot.exception.EntityDeleteException;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.CurvePointRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CurveService {

    private final CurvePointRepository curvePointRepository;

    /**
     * Constructs a new instance of {@link CurveService}.
     *
     * @param curvePointRepository the repository for accessing curve point data
     */
    public CurveService(CurvePointRepository curvePointRepository) {
        this.curvePointRepository = curvePointRepository;
    }

    /**
     * Converts a {@link CurvePoint} entity to a {@link CurveDTO}.
     *
     * @param curvePoint the curve point entity to convert
     * @return the converted {@link CurveDTO}
     */
    public CurveDTO convertToDTO(CurvePoint curvePoint) {
        log.info("Converting curve point {}", curvePoint);

        CurveDTO curveDTO = new CurveDTO();
        curveDTO.setId(curvePoint.getId());
        curveDTO.setCurveId(curvePoint.getCurveId());
        curveDTO.setTerm(curvePoint.getTerm());
        curveDTO.setValue(curvePoint.getValue());

        return curveDTO;
    }

    /**
     * Converts a list of {@link CurvePoint} entities to a list of {@link CurveDTO}s.
     *
     * @param curvePointList the list of curve point entities to convert
     * @return the list of converted {@link CurveDTO}s
     */
    public List<CurveDTO> convertToDtoList(List<CurvePoint> curvePointList) {
        log.info("Converting list of curve to DTO");

        return curvePointList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all curve points as a list.
     *
     * @return a list of all {@link CurvePoint} entities
     */
    public List<CurvePoint> getAllCurvePoint() {
        return curvePointRepository.findAll();
    }

    /**
     * Retrieves a {@link CurveDTO} by its ID.
     *
     * @param id the ID of the curve point to retrieve
     * @return the retrieved {@link CurveDTO}
     * @throws IllegalArgumentException  if the ID is invalid
     * @throws EntityNotFoundException   if no curve point is found with the given ID
     */
    public CurveDTO getCurveDTOById(int id) {
        log.info("Fetching curve point {}", id);

        if(id<=0){
            log.error("Invalid id.");
            throw new IllegalArgumentException("ID must be a positive integer.");
        }

        CurvePoint curvePoint=curvePointRepository.findById(id).orElseThrow(()->
                new EntityNotFoundException("Curve point with ID " + id + " not found"));

        return convertToDTO(curvePoint);
    }

    /**
     * Saves a new curve point based on a {@link CurveDTO}.
     *
     * @param curveDTO the data transfer object containing curve point details
     * @return the saved {@link CurvePoint} entity
     * @throws IllegalArgumentException  if the {@link CurveDTO} is null
     * @throws EntitySaveException       if saving the curve point fails
     */
    public CurvePoint saveCurvePoint(CurveDTO curveDTO) {
        log.info("Saving a new curve into the curve point {}", curveDTO);

        if (curveDTO == null) {
            log.error("Curve is null, cannot save.");
            throw new IllegalArgumentException("Curve cannot be null.");
        }

        CurvePoint curvePoint = new CurvePoint();
        curvePoint.setCurveId(curveDTO.getCurveId());
        curvePoint.setTerm(curveDTO.getTerm());
        curvePoint.setValue(curveDTO.getValue());
        curvePoint.setCreationDate(LocalDateTime.now());

        try {
           CurvePoint saveCurve = curvePointRepository.save(curvePoint);
            log.info("Added curve point {}", curvePoint);
            return saveCurve;
        } catch (DataAccessException e) {
            log.error("Error while adding curve point {}", curvePoint, e);
            throw new EntitySaveException("Failed to add curve point with ID " + curveDTO.getCurveId(), e);
        }

    }

    /**
     * Updates an existing curve point based on its ID and a {@link CurveDTO}.
     *
     * @param id       the ID of the curve point to update
     * @param curveDTO the data transfer object containing updated curve point details
     * @return the updated {@link CurvePoint} entity
     * @throws IllegalArgumentException  if the {@link CurveDTO} is null
     * @throws EntityNotFoundException   if no curve point is found with the given ID
     * @throws EntitySaveException       if updating the curve point fails
     */
    public CurvePoint updateCurvePoint(int id, CurveDTO curveDTO) {
        log.info("Updating curve point {}", curveDTO);

        if (curveDTO == null) {
            log.error("Curve DTO is null, cannot update.");
            throw new IllegalArgumentException("Curve DTO cannot be null.");
        }

        CurvePoint curvePoint=curvePointRepository.findById(id).orElseThrow(()->
                new EntityNotFoundException("Curve point with ID " + id + " not found"));

        curvePoint.setTerm(curveDTO.getTerm());
        curvePoint.setCurveId(curveDTO.getCurveId());
        curvePoint.setValue(curveDTO.getValue());

        try{
          CurvePoint saveCurve=  curvePointRepository.save(curvePoint);
            log.info("Updated curve point {}", curvePoint);
            return saveCurve;
        }catch (DataAccessException e) {
            log.error("Error while updating curve point {}", curvePoint, e);
            throw new EntitySaveException("Failed to update curve point with ID " + curveDTO.getCurveId(), e);
        }
    }

    /**
     * Deletes a curve point by its ID.
     *
     * @param id the ID of the curve point to delete
     * @throws IllegalArgumentException   if the ID is invalid
     * @throws EntityNotFoundException    if no curve point is found with the given ID
     * @throws EntityDeleteException      if deleting the curve point fails
     */
    public void deleteCurvePoint(int id) {
        log.info("Deleting curve point {}", id);

        if (id <= 0) {
            log.error("Invalid ID: {}", id);
            throw new IllegalArgumentException("ID must be a positive integer.");
        }

        if (!curvePointRepository.existsById(id)) {
            log.info("Curve point with ID {} not found",id);
            throw new EntityNotFoundException("Curve point with ID {} not found",id);
        }

        try {
            curvePointRepository.deleteById(id);
            log.info("Deleted curve point {}", id);
        }catch (Exception e) {
            log.error("Error while deleting curve point {}", id, e);
            throw new EntityDeleteException("Failed to delete curve point with ID " + id, e);
        }
    }

}
