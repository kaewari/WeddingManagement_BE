/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qltc.pojo;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author sonho
 */
@Getter
@Setter
@Entity
@Table(name = "halls")
public class Hall implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    private String name;
    @Basic(optional = false)
    private String tableCount;
    @Basic(optional = false)
    private String guestUpTo;
    @Basic(optional = false)
    private String description;
    @Basic(optional = false)
    private Boolean isActive;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hallId")
    private Set<HallPrice> hallPriceSet;
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User userId;

}
