import cv2
import firebase_admin
from firebase_admin import credentials, firestore
from datetime import datetime, timezone
import RPi.GPIO as GPIO

def initialize_firebase():
    """Initialize Firebase Admin SDK and Firestore client."""
    cred = credentials.Certificate('key.json')
    firebase_admin.initialize_app(cred)
    return firestore.client()

def setup_gpio(camera_pins):
    """Setup GPIO pins."""
    GPIO.setmode(GPIO.BCM)
    GPIO.setwarnings(False)
    for cam in camera_pins:
        GPIO.setup(cam, GPIO.OUT)
        GPIO.output(cam, GPIO.LOW)

def get_current_datetime():
    """Get current date and time as timezone-aware."""
    now = datetime.now(timezone.utc)
    return now

def verify_qr_code(db, data):
    """Verify if the QR code data is in the Firestore database and if the current time is within check-in and check-out dates."""
    users_ref = db.collection('reservations')
    # print(data)
    query = users_ref.where('qrCodeName', '==', data).get()
    if query:
        print("Query successful: QR code data found in database.")
        for doc in query:
            doc_data = doc.to_dict()
            # print(f"Document data: {doc_data}")
            check_in_date = doc_data['checkInDate']
            check_out_date = doc_data['checkOutDate']
            current_time = get_current_datetime()
            if check_in_date <= current_time <= check_out_date:
                print("Current time is within check-in and check-out dates.")
                return True
            else:
                print("Current time is outside check-in and check-out dates.")
                return False
    else:
        print("Query unsuccessful: QR code data not found in database.")
        return False

def control_cameras(camera_pins, data):
    """Control cameras based on QR code data."""
    if data == '1':
        GPIO.output(camera_pins[0], GPIO.HIGH)
        GPIO.output(camera_pins[1], GPIO.LOW)
        GPIO.output(camera_pins[2], GPIO.LOW)
        GPIO.output(camera_pins[3], GPIO.LOW)
        GPIO.output(camera_pins[4], GPIO.LOW)
    elif data == '2':
        GPIO.output(camera_pins[0], GPIO.LOW)
        GPIO.output(camera_pins[1], GPIO.HIGH)
        GPIO.output(camera_pins[2], GPIO.LOW)
        GPIO.output(camera_pins[3], GPIO.LOW)
        GPIO.output(camera_pins[4], GPIO.LOW)
    elif data == '3':
        GPIO.output(camera_pins[0], GPIO.LOW)
        GPIO.output(camera_pins[1], GPIO.LOW)
        GPIO.output(camera_pins[2], GPIO.HIGH)
        GPIO.output(camera_pins[3], GPIO.LOW)
        GPIO.output(camera_pins[4], GPIO.LOW)
    elif data == '4':
        GPIO.output(camera_pins[0], GPIO.LOW)
        GPIO.output(camera_pins[1], GPIO.LOW)
        GPIO.output(camera_pins[2], GPIO.LOW)
        GPIO.output(camera_pins[3], GPIO.HIGH)
        GPIO.output(camera_pins[4], GPIO.LOW)
    elif data == 'error':
        GPIO.output(camera_pins[0], GPIO.LOW)
        GPIO.output(camera_pins[1], GPIO.LOW)
        GPIO.output(camera_pins[2], GPIO.LOW)
        GPIO.output(camera_pins[3], GPIO.LOW)
        GPIO.output(camera_pins[4], GPIO.HIGH)

def test_qr_code(db):
    """Test the QR code '4_Denys1717070894064.png' in the Firestore database."""
    test_data = '4_Denys1717070894064.png'
    print(f"Testing QR code: {test_data}")
    if verify_qr_code(db, test_data):
        print("Test successful: QR code '4_Denys1717070894064' is valid and found in the database.")
    else:
        print("Test failed: QR code '4_Denys1717070894064' is not found in the database or current time is outside the valid range.")

def print_all_reservations(db):
    """Print all documents in the 'reservations' collection."""
    print("Printing all documents in 'reservations' collection:")
    users_ref = db.collection('reservations')
    all_docs = users_ref.get()
    for doc in all_docs:
        print(f"Document ID: {doc.id}, Data: {doc.to_dict()}")

def main():
    camera_pins = [23, 12, 26, 20, 13]
    setup_gpio(camera_pins)

    print(cv2.__version__)
    
    db = initialize_firebase()

    
    # print_all_reservations(db)
    
    # # Run a test for the specific QR code
    # test_qr_code(db)

    # Set up camera object
    cap = cv2.VideoCapture(0)
    
    # QR code detection object
    detector = cv2.QRCodeDetector()
    
    try:
        while True:
            ret, img = cap.read()
            if not ret:
                print("Failed to grab frame")
                break

            # Detect and decode the QR code
            data, bbox, _ = detector.detectAndDecode(img)
            if bbox is not None and len(bbox) > 0:
                for i in range(len(bbox[0])):
                    pt1 = (int(bbox[0][i][0]), int(bbox[0][i][1]))
                    pt2 = (int(bbox[0][(i + 1) % len(bbox[0])][0]), int(bbox[0][(i + 1) % len(bbox[0])][1]))
                    cv2.line(img, pt1, pt2, color=(255, 0, 255), thickness=2)
                if data:
                    cv2.putText(img, data, (pt1[0], pt1[1] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)
                    # print("Data found:", data)
                    data = data +".png"
                    if verify_qr_code(db, data):
                        print("QR code is valid")
                        control_cameras(camera_pins, data[0])
                    else:
                        control_cameras(camera_pins, "error")
                        print("QR code is invalid or current time is outside the valid range.")
            cv2.imshow("QR Code Detector", img)
            if cv2.waitKey(1) == ord("q"):
                break
    finally:
        cap.release()
        cv2.destroyAllWindows()
        GPIO.cleanup()

if __name__ == "__main__":
    main()
