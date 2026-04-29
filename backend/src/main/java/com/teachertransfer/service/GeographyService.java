package com.teachertransfer.service;

import com.teachertransfer.dto.geography.BlockResponse;
import com.teachertransfer.dto.geography.DistrictResponse;
import com.teachertransfer.entity.Block;
import com.teachertransfer.entity.District;
import com.teachertransfer.repository.BlockRepository;
import com.teachertransfer.repository.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GeographyService {

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private BlockRepository blockRepository;

    public List<DistrictResponse> getAllDistricts() {
        List<District> districts = districtRepository.findAllByOrderByName();
        return districts.stream()
                .map(this::mapToDistrictResponse)
                .collect(Collectors.toList());
    }

    public List<DistrictResponse> searchDistricts(String name) {
        List<District> districts = districtRepository.searchByName(name);
        return districts.stream()
                .map(this::mapToDistrictResponse)
                .collect(Collectors.toList());
    }

    public DistrictResponse getDistrictById(Integer districtId) {
        District district = districtRepository.findById(districtId)
                .orElseThrow(() -> new RuntimeException("District not found"));
        return mapToDistrictResponse(district);
    }

    public List<BlockResponse> getBlocksByDistrict(Integer districtId) {
        List<Block> blocks = blockRepository.findByDistrictIdOrderByNameAsc(districtId);
        return blocks.stream()
                .map(block -> mapToBlockResponse(block, districtId))
                .collect(Collectors.toList());
    }

    public List<BlockResponse> searchBlocks(String name) {
        List<Block> blocks = blockRepository.searchByName(name);
        return blocks.stream()
                .map(block -> mapToBlockResponse(block, block.getDistrictId()))
                .collect(Collectors.toList());
    }

    public List<BlockResponse> searchBlocksByDistrict(Integer districtId, String name) {
        List<Block> blocks = blockRepository.searchByDistrictAndName(districtId, name);
        return blocks.stream()
                .map(block -> mapToBlockResponse(block, districtId))
                .collect(Collectors.toList());
    }

    public BlockResponse getBlockById(Integer blockId) {
        Block block = blockRepository.findById(blockId)
                .orElseThrow(() -> new RuntimeException("Block not found"));
        return mapToBlockResponse(block, block.getDistrictId());
    }

    private DistrictResponse mapToDistrictResponse(District district) {
        return new DistrictResponse(
                district.getId(),
                district.getName(),
                district.getNameHindi(),
                district.getCode(),
                district.getLat(),
                district.getLng()
        );
    }

    private BlockResponse mapToBlockResponse(Block block, Integer districtId) {
        District district = districtRepository.findById(districtId).orElse(null);
        String districtName = district != null ? district.getName() : null;

        return new BlockResponse(
                block.getId(),
                districtId,
                districtName,
                block.getName(),
                block.getNameHindi(),
                block.getCode(),
                block.getLat(),
                block.getLng()
        );
    }
}