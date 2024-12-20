package com.nnk.springboot.service;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.dto.BidDTO;
import com.nnk.springboot.dto.CurveDTO;
import com.nnk.springboot.exception.EntityNotFoundException;
import com.nnk.springboot.exception.EntitySaveException;
import com.nnk.springboot.repositories.CurvePointRepository;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CurveService {

    private final CurvePointRepository curvePointRepository;

    public CurveService(CurvePointRepository curvePointRepository) {
        this.curvePointRepository = curvePointRepository;
    }

    private void validateCurveDTO(CurveDTO curveDTO) {
        if (curveDTO.getCurveId() == null || curveDTO.getCurveId() < 0) {
            throw new IllegalArgumentException("Account cannot be null or empty");
        }
        if (curveDTO.getTerm() == null || curveDTO.getTerm() < 0) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }
        if (curveDTO.getValue() == null ) {
            throw new IllegalArgumentException("Bid quantity must be positive and non-null");
        }
    }



    public CurveDTO convertToDto(CurvePoint curvePoint) {
        log.info("Converting curve point {}", curvePoint);

        CurveDTO curveDTO = new CurveDTO();
        curveDTO.setId(curvePoint.getId());
        curveDTO.setCurveId(curvePoint.getCurveId());
        curveDTO.setTerm(curvePoint.getTerm());
        curveDTO.setValue(curvePoint.getValue());

        return curveDTO;

    }

    public List<CurveDTO> convertToDtoList(List<CurvePoint> curvePointList) {
        log.info("Converting curve point list {}", curvePointList);

        return curvePointList.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<CurvePoint> getCurvePointList() {
        return curvePointRepository.findAll();
    }

    public void addCurvePoint(CurveDTO curveDTO) {
        log.info("Adding curve point {}", curveDTO);

        validateCurveDTO(curveDTO);

        CurvePoint curvePoint = new CurvePoint();
        curvePoint.setCurveId(curveDTO.getCurveId());
        curvePoint.setTerm(curveDTO.getTerm());
        curvePoint.setValue(curveDTO.getValue());

        try {
            curvePointRepository.save(curvePoint);
            log.info("Added curve point {}", curvePoint);
        } catch (Exception e) {
            log.error("Error while adding curve point {}", curvePoint, e);
            throw new EntitySaveException("Failed to add curve point with ID " + curveDTO.getCurveId(), e);
        }

    }

    public CurveDTO getCurveDTOById(int id) {
        CurvePoint curvePoint=curvePointRepository.findById(id).orElseThrow(()->
                new EntityNotFoundException("Curve point with ID " + id + " not found"));

        return convertToDto(curvePoint);
    }

    public void updateCurvePoint(int id, CurveDTO curveDTO) {
        log.info("Updating curve point {}", curveDTO);

        validateCurveDTO(curveDTO);

        CurvePoint curvePoint=curvePointRepository.findById(id).orElseThrow(()->
                new EntityNotFoundException("Curve point with ID " + id + " not found"));
        curvePoint.setTerm(curveDTO.getTerm());
        curvePoint.setCurveId(curveDTO.getCurveId());
        curvePoint.setValue(curveDTO.getValue());

        try{
            curvePointRepository.save(curvePoint);
            log.info("Updated curve point {}", curvePoint);
        }catch (Exception e) {
            log.error("Error while updating curve point {}", curvePoint, e);
            throw new EntitySaveException("Failed to update curve point with ID " + curveDTO.getCurveId(), e);
        }
    }

    public void deleteCurvePoint(int id) {
        log.info("Deleting curve point {}", id);

        if (!curvePointRepository.existsById(id)) {
            log.error("Curve point with ID " + id + " not found");
            throw new EntityNotFoundException("Curve point with ID " + id + " not found");
        }

        try {
            curvePointRepository.deleteById(id);
            log.info("Deleted curve point {}", id);
        }catch (Exception e) {
            log.error("Error while deleting curve point {}", id, e);
            throw new EntitySaveException("Failed to delete curve point with ID " + id, e);
        }
    }

}
