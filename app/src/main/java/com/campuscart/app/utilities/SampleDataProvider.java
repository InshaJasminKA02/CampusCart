package com.campuscart.app.utilities;

import com.campuscart.app.models.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Provides rich, realistic sample campus listings for offline preview, UI verification,
 * and testing before backend credentials are configured.
 */
public final class SampleDataProvider {
    private SampleDataProvider() {}

    public static List<Product> getSampleProducts() {
        List<Product> products = new ArrayList<>();
        long now = System.currentTimeMillis();
        long hour = 3600 * 1000L;

        // 1. Books
        products.add(new Product(
                "prod_1",
                "Higher Engineering Mathematics - B.S. Grewal (44th Ed)",
                "Standard engineering reference textbook in clean condition. No ink markings or torn pages. Covers calculus, linear algebra, and differential equations.",
                480.0,
                Constants.CATEGORY_BOOKS,
                Constants.CONDITION_LIKE_NEW,
                new ArrayList<>(Collections.singletonList("https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&auto=format&fit=crop&q=80")),
                "seller_rahul",
                "Rahul Sharma (CSE '26)",
                now - (2 * hour),
                false
        ));

        // 2. Electronics
        products.add(new Product(
                "prod_2",
                "Casio FX-991EX ClassWiz Scientific Calculator",
                "Genuine scientific calculator approved for university exams. High-resolution LCD display with dual solar and battery power. Comes with original protective cover.",
                850.0,
                Constants.CATEGORY_ELECTRONICS,
                Constants.CONDITION_BRAND_NEW,
                new ArrayList<>(Collections.singletonList("https://images.unsplash.com/photo-1594980596870-8aa52a78d8cd?w=600&auto=format&fit=crop&q=80")),
                "seller_ananya",
                "Ananya Verma (ECE '25)",
                now - (5 * hour),
                false
        ));

        // 3. Electronics / Audio
        products.add(new Product(
                "prod_3",
                "Wireless Over-Ear Studio Headphones with ANC",
                "Matte black active noise cancellation headphones, perfect for hostel study sessions and library focus. Battery lasts 28+ hours. Type-C charging cable included.",
                1499.0,
                Constants.CATEGORY_ELECTRONICS,
                Constants.CONDITION_GOOD,
                new ArrayList<>(Collections.singletonList("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80")),
                "seller_vikram",
                "Vikram Patel (Mech '24)",
                now - (12 * hour),
                false
        ));

        // 4. Stationery
        products.add(new Product(
                "prod_4",
                "Rotring Drafting Compass & Engineering Drawing Set",
                "Professional engineering drawing instrument box with precision extension bar, spare leads, and ruling pen. Used only for one semester.",
                350.0,
                Constants.CATEGORY_STATIONERY,
                Constants.CONDITION_LIKE_NEW,
                new ArrayList<>(Collections.singletonList("https://images.unsplash.com/photo-1589998059171-988d887df646?w=600&auto=format&fit=crop&q=80")),
                "seller_priya",
                "Priya Nair (Civil '26)",
                now - (18 * hour),
                false
        ));

        // 5. Clothing
        products.add(new Product(
                "prod_5",
                "Unisex White Cotton Chemistry Lab Apron (Size L)",
                "Full-length premium cotton lab coat with two front utility pockets. Clean, sanitized, and university lab compliant.",
                220.0,
                Constants.CATEGORY_CLOTHING,
                Constants.CONDITION_GOOD,
                new ArrayList<>(Collections.singletonList("https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600&auto=format&fit=crop&q=80")),
                "seller_kavita",
                "Kavita S. (Biotech '25)",
                now - (24 * hour),
                false
        ));

        // 6. Sports
        products.add(new Product(
                "prod_6",
                "Yonex Muscle Power Badminton Racket + Thermal Bag",
                "Lightweight carbon graphite frame strung at 24 lbs with fresh grip tape. Great for hostel matches and sports complex play.",
                799.0,
                Constants.CATEGORY_SPORTS,
                Constants.CONDITION_GOOD,
                new ArrayList<>(Collections.singletonList("https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=600&auto=format&fit=crop&q=80")),
                "seller_arjun",
                "Arjun Mehta (ECE '25)",
                now - (30 * hour),
                false
        ));

        // 7. Furniture
        products.add(new Product(
                "prod_7",
                "Foldable Wooden Study Desk with Tablet & Cup Slot",
                "Ergonomic portable laptop desk with foldable metal legs. Ideal for hostel bed study sessions and late-night assignments.",
                450.0,
                Constants.CATEGORY_FURNITURE,
                Constants.CONDITION_LIKE_NEW,
                new ArrayList<>(Collections.singletonList("https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=600&auto=format&fit=crop&q=80")),
                "seller_siddharth",
                "Siddharth Rao (IT '26)",
                now - (40 * hour),
                false
        ));

        // 8. Accessories
        products.add(new Product(
                "prod_8",
                "Anti-Theft Water-Resistant Laptop Backpack (30L)",
                "Spacious multi-compartment college backpack with padded 15.6 inch laptop sleeve, external USB charging port, and hidden passport pocket.",
                690.0,
                Constants.CATEGORY_ACCESSORIES,
                Constants.CONDITION_GOOD,
                new ArrayList<>(Collections.singletonList("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600&auto=format&fit=crop&q=80")),
                "seller_rohit",
                "Rohit K. (MBA '25)",
                now - (48 * hour),
                false
        ));

        // 9. Books
        products.add(new Product(
                "prod_9",
                "Operating System Concepts (Silberschatz Dinosaur Book)",
                "Tenth edition global paperback. Essential for OS theory, process synchronization, memory management, and file systems.",
                520.0,
                Constants.CATEGORY_BOOKS,
                Constants.CONDITION_FAIR,
                new ArrayList<>(Collections.singletonList("https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=600&auto=format&fit=crop&q=80")),
                "seller_neha",
                "Neha Sen (CSE '25)",
                now - (60 * hour),
                false
        ));

        return products;
    }
}
