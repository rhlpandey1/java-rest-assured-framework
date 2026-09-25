import requests


BASE_URL = "https://restful-booker.herokuapp.com"


def test_booking_crud_e2e():

    # =====================================================
    # 1. POST - Authenticate
    # =====================================================

    auth_payload = {
        "username": "admin",
        "password": "password123"
    }

    auth_response = requests.post(
        f"{BASE_URL}/auth",
        json=auth_payload
    )

    assert auth_response.status_code == 200

    token = auth_response.json()["token"]

    print(f"Token: {token}")


    # =====================================================
    # 2. POST - Create Booking
    # =====================================================

    create_booking_payload = {
        "firstname": "Rahul",
        "lastname": "Test",
        "totalprice": 150,
        "depositpaid": True,
        "bookingdates": {
            "checkin": "2026-10-01",
            "checkout": "2026-10-05"
        },
        "additionalneeds": "Breakfast"
    }

    create_response = requests.post(
        f"{BASE_URL}/booking",
        json=create_booking_payload
    )

    assert create_response.status_code == 200

    create_data = create_response.json()

    assert "bookingid" in create_data
    assert create_data["booking"]["firstname"] == "Rahul"

    booking_id = create_data["bookingid"]

    print(f"Created Booking ID: {booking_id}")


    # =====================================================
    # 3. GET - Retrieve Booking
    # =====================================================

    get_response = requests.get(
        f"{BASE_URL}/booking/{booking_id}"
    )

    assert get_response.status_code == 200

    get_data = get_response.json()

    assert get_data["firstname"] == "Rahul"
    assert get_data["lastname"] == "Test"
    assert get_data["totalprice"] == 150

    print("GET validation successful")


    # =====================================================
    # 4. PUT - Replace Complete Booking
    # =====================================================

    put_payload = {
        "firstname": "RahulUpdated",
        "lastname": "Automation",
        "totalprice": 250,
        "depositpaid": False,
        "bookingdates": {
            "checkin": "2026-11-01",
            "checkout": "2026-11-10"
        },
        "additionalneeds": "Lunch"
    }

    headers = {
        "Cookie": f"token={token}",
        "Content-Type": "application/json"
    }

    put_response = requests.put(
        f"{BASE_URL}/booking/{booking_id}",
        json=put_payload,
        headers=headers
    )

    assert put_response.status_code == 200

    put_data = put_response.json()

    assert put_data["firstname"] == "RahulUpdated"
    assert put_data["totalprice"] == 250

    print("PUT validation successful")


    # =====================================================
    # 5. GET - Verify PUT
    # =====================================================

    get_response = requests.get(
        f"{BASE_URL}/booking/{booking_id}"
    )

    assert get_response.status_code == 200

    get_data = get_response.json()

    assert get_data["firstname"] == "RahulUpdated"
    assert get_data["lastname"] == "Automation"
    assert get_data["totalprice"] == 250


    # =====================================================
    # 6. PATCH - Partial Update
    # =====================================================

    patch_payload = {
        "firstname": "RahulPatched"
    }

    patch_response = requests.patch(
        f"{BASE_URL}/booking/{booking_id}",
        json=patch_payload,
        headers=headers
    )

    assert patch_response.status_code == 200

    patch_data = patch_response.json()

    assert patch_data["firstname"] == "RahulPatched"

    print("PATCH validation successful")


    # =====================================================
    # 7. GET - Verify PATCH
    # =====================================================

    get_response = requests.get(
        f"{BASE_URL}/booking/{booking_id}"
    )

    assert get_response.status_code == 200

    get_data = get_response.json()

    assert get_data["firstname"] == "RahulPatched"
    assert get_data["lastname"] == "Automation"


    # =====================================================
    # 8. DELETE
    # =====================================================

    delete_response = requests.delete(
        f"{BASE_URL}/booking/{booking_id}",
        headers=headers
    )

    assert delete_response.status_code == 201

    print("DELETE successful")


    # =====================================================
    # 9. GET - Verify Delete
    # =====================================================

    get_response = requests.get(
        f"{BASE_URL}/booking/{booking_id}"
    )

    assert get_response.status_code == 404

    print("Delete validation successful")